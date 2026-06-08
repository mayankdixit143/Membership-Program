package com.firstclub.membership.entity;

import com.firstclub.membership.enums.TierType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Immutable audit trail for every subscription state change
 * (subscribe, upgrade, downgrade, cancel, expire).
 */
@Entity
@Table(name = "subscription_audit_logs",
       indexes = @Index(name = "idx_audit_sub_id", columnList = "subscription_id"))
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    private UserSubscription subscription;

    @Column(nullable = false)
    private String action;
    @Enumerated(EnumType.STRING)
    private TierType fromTier;

    @Enumerated(EnumType.STRING)
    private TierType toTier;

    private String remarks;

    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
