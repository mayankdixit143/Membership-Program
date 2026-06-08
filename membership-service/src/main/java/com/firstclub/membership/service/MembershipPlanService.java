package com.firstclub.membership.service;

import com.firstclub.membership.dto.PlanResponse;
import com.firstclub.membership.dto.TierBenefitResponse;
import com.firstclub.membership.dto.TierResponse;
import com.firstclub.membership.entity.MembershipPlan;
import com.firstclub.membership.enums.TierType;
import com.firstclub.membership.exception.ResourceNotFoundException;
import com.firstclub.membership.repository.MembershipPlanRepository;
import com.firstclub.membership.repository.TierBenefitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MembershipPlanService {

    private final MembershipPlanRepository planRepository;
    private final TierBenefitRepository tierBenefitRepository;

    public List<PlanResponse> getAllActivePlans() {
        return planRepository.findAllByActiveTrue().stream()
                .map(this::toResponse)
                .toList();
    }

    public PlanResponse getPlanById(Long planId) {
        return planRepository.findById(planId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found: " + planId));
    }

    public List<TierResponse> getAllTiersWithBenefits() {
        return Arrays.stream(TierType.values())
                .map(tier -> TierResponse.builder()
                        .tierType(tier)
                        .benefits(getBenefitsForTier(tier))
                        .build())
                .toList();
    }

    public List<TierBenefitResponse> getBenefitsForTier(TierType tier) {
        return tierBenefitRepository.findByTierTypeAndActiveTrue(tier).stream()
                .map(b -> TierBenefitResponse.builder()
                        .tierType(b.getTierType())
                        .benefitType(b.getBenefitType())
                        .benefitValue(b.getBenefitValue())
                        .description(b.getDescription())
                        .build())
                .toList();
    }

    public MembershipPlan getPlanEntityByType(com.firstclub.membership.enums.PlanType planType) {
        return planRepository.findByPlanTypeAndActiveTrue(planType)
                .orElseThrow(() -> new ResourceNotFoundException("Active plan not found for type: " + planType));
    }

    private PlanResponse toResponse(MembershipPlan plan) {
        return PlanResponse.builder()
                .id(plan.getId())
                .planType(plan.getPlanType())
                .price(plan.getPrice())
                .description(plan.getDescription())
                .promoLabel(plan.getPromoLabel())
                .durationMonths(plan.getPlanType().getDurationMonths())
                .build();
    }
}
