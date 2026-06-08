package com.firstclub.membership.entity;

import com.firstclub.membership.enums.BenefitType;
import com.firstclub.membership.enums.TierType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Configurable benefit linked to a specific tier.
 * Adding / modifying benefits requires no code changes — just DB records.
 *
 * Examples:
 *   SILVER  -> FREE_DELIVERY          (value: null)
 *   GOLD    -> DISCOUNT_PERCENTAGE    (value: "5")   => 5% discount
 *   PLATINUM-> PRIORITY_SUPPORT       (value: null)
 */
@Entity
@Table(name = "tier_benefits",
       uniqueConstraints = @UniqueConstraint(columnNames = {"tierType", "benefitType"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TierBenefit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TierType tierType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BenefitType benefitType;

    /** Numeric or string value for parameterised benefits (e.g. discount %) */
    private String benefitValue;

    private String description;

    @Column(nullable = false)
    private boolean active = true;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
