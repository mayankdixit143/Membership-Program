package com.firstclub.membership.strategy;

import com.firstclub.membership.entity.TierCriteria;
import com.firstclub.membership.entity.User;
import com.firstclub.membership.enums.TierCriteriaType;

/**
 * Strategy interface for evaluating a single TierCriteria against a User.
 *
 * Each concrete implementation handles one TierCriteriaType.
 * New criteria types can be added without touching existing code (Open/Closed Principle).
 */
public interface TierCriteriaEvaluator {

    /** The criteria type this evaluator handles. */
    TierCriteriaType getSupportedType();

    /**
     * Returns true if the user satisfies the given criteria.
     *
     * @param user     the user being evaluated
     * @param criteria the criteria configuration record
     */
    boolean evaluate(User user, TierCriteria criteria);
}
