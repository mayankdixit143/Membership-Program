package com.firstclub.membership.strategy;

import com.firstclub.membership.entity.TierCriteria;
import com.firstclub.membership.entity.User;
import com.firstclub.membership.enums.TierCriteriaType;
import com.firstclub.membership.repository.UserOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Evaluates whether a user has placed >= threshold orders in the last 30 days.
 */
@Component
@RequiredArgsConstructor
public class OrderCountEvaluator implements TierCriteriaEvaluator {

    private final UserOrderRepository userOrderRepository;

    @Override
    public TierCriteriaType getSupportedType() {
        return TierCriteriaType.MIN_ORDER_COUNT;
    }

    @Override
    public boolean evaluate(User user, TierCriteria criteria) {
        long threshold = Long.parseLong(criteria.getThresholdValue());
        LocalDateTime since = LocalDateTime.now().minusDays(30);
        long count = userOrderRepository.countOrdersSince(user.getId(), since);
        return count >= threshold;
    }
}
