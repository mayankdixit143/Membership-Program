package com.firstclub.membership.entity;

import com.firstclub.membership.enums.TierCriteriaType;
import com.firstclub.membership.enums.TierType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Configurable criteria that a user must meet to qualify for a given tier.
 * All criteria for a tier are evaluated with AND logic unless overridden.
 *
 * Examples:
 *   GOLD    -> MIN_ORDER_COUNT  (thresholdValue: "5")   => at least 5 orders
 *   GOLD    -> MIN_ORDER_VALUE  (thresholdValue: "2000") => ₹2000 total monthly spend
 *   PLATINUM-> USER_COHORT      (cohortTag: "vip")
 */
@Entity
@Table(name = "tier_criteria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TierCriteria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TierType tierType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TierCriteriaType criteriaType;

    /** Threshold value for numeric criteria (order count / order value) */
    private String thresholdValue;

    /** For USER_COHORT criteria: identifies the cohort tag */
    private String cohortTag;

    @Column(nullable = false)
    private boolean active = true;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
