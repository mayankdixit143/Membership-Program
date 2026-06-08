package com.firstclub.membership.strategy;

import com.firstclub.membership.entity.TierCriteria;
import com.firstclub.membership.entity.User;
import com.firstclub.membership.enums.TierCriteriaType;
import org.springframework.stereotype.Component;

/**
 * Evaluates whether a user belongs to the required cohort tag.
 */
@Component
public class UserCohortEvaluator implements TierCriteriaEvaluator {

    @Override
    public TierCriteriaType getSupportedType() {
        return TierCriteriaType.USER_COHORT;
    }

    @Override
    public boolean evaluate(User user, TierCriteria criteria) {
        String requiredCohort = criteria.getCohortTag();
        return requiredCohort != null && user.getCohortTags().contains(requiredCohort);
    }
}
