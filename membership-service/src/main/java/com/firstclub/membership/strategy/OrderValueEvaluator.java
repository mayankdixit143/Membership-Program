package com.firstclub.membership.strategy;

import com.firstclub.membership.entity.TierCriteria;
import com.firstclub.membership.entity.User;
import com.firstclub.membership.enums.TierCriteriaType;
import com.firstclub.membership.repository.UserOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Evaluates whether a user's total order value in the last 30 days meets threshold.
 */
@Component
@RequiredArgsConstructor
public class OrderValueEvaluator implements TierCriteriaEvaluator {

    private final UserOrderRepository userOrderRepository;

    @Override
    public TierCriteriaType getSupportedType() {
        return TierCriteriaType.MIN_ORDER_VALUE;
    }

    @Override
    public boolean evaluate(User user, TierCriteria criteria) {
        BigDecimal threshold = new BigDecimal(criteria.getThresholdValue());
        LocalDateTime since = LocalDateTime.now().minusDays(30);
        BigDecimal totalValue = userOrderRepository.sumOrderValueSince(user.getId(), since);
        return totalValue.compareTo(threshold) >= 0;
    }
}
