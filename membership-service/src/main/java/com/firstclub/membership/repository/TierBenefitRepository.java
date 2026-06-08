package com.firstclub.membership.repository;

import com.firstclub.membership.entity.TierBenefit;
import com.firstclub.membership.enums.TierType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TierBenefitRepository extends JpaRepository<TierBenefit, Long> {
    List<TierBenefit> findByTierTypeAndActiveTrue(TierType tierType);
    List<TierBenefit> findAllByActiveTrue();
}
