package com.firstclub.membership.service;

import com.firstclub.membership.dto.AuditLogResponse;
import com.firstclub.membership.dto.SubscriptionResponse;
import com.firstclub.membership.dto.TierBenefitResponse;
import com.firstclub.membership.entity.*;
import com.firstclub.membership.enums.PlanType;
import com.firstclub.membership.enums.SubscriptionStatus;
import com.firstclub.membership.enums.TierType;
import com.firstclub.membership.exception.BusinessException;
import com.firstclub.membership.exception.ResourceNotFoundException;
import com.firstclub.membership.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Core subscription lifecycle service.
 *
 * Concurrency notes:
 * - All mutating operations are @Transactional.
 * - Upgrade/downgrade/cancel acquire a PESSIMISTIC_WRITE lock on the subscription row,
 *   preventing concurrent threads from mutating the same subscription simultaneously.
 * - UserSubscription also carries an @Version field for optimistic locking as a second
 *   safety net across distributed instances.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionService {

    private final UserRepository userRepository;
    private final UserSubscriptionRepository subscriptionRepository;
    private final SubscriptionAuditLogRepository auditLogRepository;
    private final MembershipPlanService planService;
    private final TierBenefitRepository tierBenefitRepository;


    @Transactional
    public SubscriptionResponse subscribe(Long userId, PlanType planType, TierType tierType) {
        User user = getUser(userId);

        if (subscriptionRepository.existsByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)) {
            throw new BusinessException("User already has an active subscription. Cancel it before subscribing to a new plan.");
        }

        MembershipPlan plan = planService.getPlanEntityByType(planType);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = now.plusMonths(planType.getDurationMonths());

        UserSubscription subscription = UserSubscription.builder()
                .user(user)
                .plan(plan)
                .tierType(tierType)
                .status(SubscriptionStatus.ACTIVE)
                .startedAt(now)
                .expiresAt(expiry)
                .build();

        subscription = subscriptionRepository.save(subscription);
        audit(subscription, "SUBSCRIBED", null, tierType, "New subscription created");
        log.info("User {} subscribed to {} plan with tier {}", userId, planType, tierType);
        return toResponse(subscription);
    }

    @Transactional
    public SubscriptionResponse upgradeTier(Long userId, TierType newTier) {
        UserSubscription subscription = getActiveSubscriptionWithLock(userId);
        TierType currentTier = subscription.getTierType();

        if (!newTier.isHigherThan(currentTier)) {
            throw new BusinessException(
                String.format("Cannot upgrade: %s is not higher than current tier %s. Use downgrade instead.", newTier, currentTier));
        }

        subscription.setTierType(newTier);
        subscription = subscriptionRepository.save(subscription);
        audit(subscription, "TIER_UPGRADED", currentTier, newTier,
              String.format("Tier upgraded from %s to %s", currentTier, newTier));

        log.info("User {} tier upgraded from {} to {}", userId, currentTier, newTier);
        return toResponse(subscription);
    }


    @Transactional
    public SubscriptionResponse downgradeTier(Long userId, TierType newTier) {
        UserSubscription subscription = getActiveSubscriptionWithLock(userId);
        TierType currentTier = subscription.getTierType();

        if (!newTier.isLowerThan(currentTier)) {
            throw new BusinessException(
                String.format("Cannot downgrade: %s is not lower than current tier %s. Use upgrade instead.", newTier, currentTier));
        }

        subscription.setTierType(newTier);
        subscription = subscriptionRepository.save(subscription);
        audit(subscription, "TIER_DOWNGRADED", currentTier, newTier,
              String.format("Tier downgraded from %s to %s", currentTier, newTier));

        log.info("User {} tier downgraded from {} to {}", userId, currentTier, newTier);
        return toResponse(subscription);
    }


    @Transactional
    public SubscriptionResponse cancelSubscription(Long userId) {
        UserSubscription subscription = getActiveSubscriptionWithLock(userId);

        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscription.setCancelledAt(LocalDateTime.now());
        subscription = subscriptionRepository.save(subscription);
        audit(subscription, "CANCELLED", subscription.getTierType(), null, "User requested cancellation");

        log.info("User {} cancelled subscription {}", userId, subscription.getId());
        return toResponse(subscription);
    }


    @Transactional(readOnly = true)
    public SubscriptionResponse getCurrentSubscription(Long userId) {
        getUser(userId);
        UserSubscription sub = subscriptionRepository
                .findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("No active subscription for user: " + userId));
        return toResponse(sub);
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponse> getSubscriptionHistory(Long subscriptionId) {
        return auditLogRepository.findBySubscriptionIdOrderByCreatedAtDesc(subscriptionId)
                .stream()
                .map(l -> AuditLogResponse.builder()
                        .id(l.getId())
                        .action(l.getAction())
                        .fromTier(l.getFromTier())
                        .toTier(l.getToTier())
                        .remarks(l.getRemarks())
                        .createdAt(l.getCreatedAt())
                        .build())
                .toList();
    }


    @Transactional
    public int expireSubscriptions() {
        List<UserSubscription> expired = subscriptionRepository
                .findByStatusAndExpiresAtBefore(SubscriptionStatus.ACTIVE, LocalDateTime.now());
        for (UserSubscription sub : expired) {
            sub.setStatus(SubscriptionStatus.EXPIRED);
            subscriptionRepository.save(sub);
            audit(sub, "EXPIRED", sub.getTierType(), null, "Subscription expired automatically");
        }
        if (!expired.isEmpty()) {
            log.info("Expired {} subscriptions", expired.size());
        }
        return expired.size();
    }


    private UserSubscription getActiveSubscriptionWithLock(Long userId) {
        UserSubscription sub = subscriptionRepository
                .findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("No active subscription for user: " + userId));
        return subscriptionRepository.findByIdWithLock(sub.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found: " + sub.getId()));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
    }

    private void audit(UserSubscription sub, String action, TierType from, TierType to, String remarks) {
        auditLogRepository.save(SubscriptionAuditLog.builder()
                .subscription(sub)
                .action(action)
                .fromTier(from)
                .toTier(to)
                .remarks(remarks)
                .build());
    }

    private SubscriptionResponse toResponse(UserSubscription sub) {
        List<TierBenefitResponse> benefits = tierBenefitRepository
                .findByTierTypeAndActiveTrue(sub.getTierType())
                .stream()
                .map(b -> TierBenefitResponse.builder()
                        .tierType(b.getTierType())
                        .benefitType(b.getBenefitType())
                        .benefitValue(b.getBenefitValue())
                        .description(b.getDescription())
                        .build())
                .toList();

        return SubscriptionResponse.builder()
                .subscriptionId(sub.getId())
                .userId(sub.getUser().getId())
                .planType(sub.getPlan().getPlanType())
                .planPrice(sub.getPlan().getPrice())
                .tierType(sub.getTierType())
                .status(sub.getStatus())
                .startedAt(sub.getStartedAt())
                .expiresAt(sub.getExpiresAt())
                .cancelledAt(sub.getCancelledAt())
                .currentBenefits(benefits)
                .active(sub.isActive())
                .build();
    }
}
