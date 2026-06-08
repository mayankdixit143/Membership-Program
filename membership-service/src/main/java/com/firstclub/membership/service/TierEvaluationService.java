package com.firstclub.membership.service;

import com.firstclub.membership.entity.TierCriteria;
import com.firstclub.membership.entity.User;
import com.firstclub.membership.enums.TierType;
import com.firstclub.membership.repository.TierCriteriaRepository;
import com.firstclub.membership.strategy.TierCriteriaEvaluator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Evaluates which tier a user qualifies for.
 *
 * Tiers are checked from highest (PLATINUM) to lowest (SILVER).
 * A user qualifies for a tier when ALL active criteria for that tier are satisfied.
 *
 * The evaluators map is built once via constructor injection from all
 * TierCriteriaEvaluator beans — adding a new evaluator requires only
 * creating a new @Component, no changes here.
 */
@Service
@Slf4j
public class TierEvaluationService {

    private final TierCriteriaRepository tierCriteriaRepository;
    private final Map<com.firstclub.membership.enums.TierCriteriaType, TierCriteriaEvaluator> evaluatorMap;

    public TierEvaluationService(TierCriteriaRepository tierCriteriaRepository,
                                  List<TierCriteriaEvaluator> evaluators) {
        this.tierCriteriaRepository = tierCriteriaRepository;
        this.evaluatorMap = evaluators.stream()
                .collect(Collectors.toMap(TierCriteriaEvaluator::getSupportedType, Function.identity()));
    }

    /**
     * Determines the best tier a user qualifies for.
     * Falls back to SILVER (base tier) if no higher tier criteria are met.
     */
    public TierType evaluateTierForUser(User user) {
        TierType[] tiers = {TierType.PLATINUM, TierType.GOLD, TierType.SILVER};
        for (TierType tier : tiers) {
            if (userQualifiesForTier(user, tier)) {
                log.debug("User {} qualifies for tier {}", user.getId(), tier);
                return tier;
            }
        }
        return TierType.SILVER;
    }

    /**
     * Returns true if the user satisfies ALL active criteria for the given tier.
     * A tier with no criteria configured is considered auto-qualifying (base tier = SILVER).
     */
    public boolean userQualifiesForTier(User user, TierType tier) {
        List<TierCriteria> criteriaList = tierCriteriaRepository.findByTierTypeAndActiveTrue(tier);
        if (criteriaList.isEmpty()) {
            return tier == TierType.SILVER;
        }
        return criteriaList.stream().allMatch(criteria -> evaluate(user, criteria));
    }

    private boolean evaluate(User user, TierCriteria criteria) {
        TierCriteriaEvaluator evaluator = evaluatorMap.get(criteria.getCriteriaType());
        if (evaluator == null) {
            log.warn("No evaluator found for criteria type: {}", criteria.getCriteriaType());
            return false;
        }
        return evaluator.evaluate(user, criteria);
    }
}
