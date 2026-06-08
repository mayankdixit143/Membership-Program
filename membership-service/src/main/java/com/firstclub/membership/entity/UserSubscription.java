package com.firstclub.membership.entity;

import com.firstclub.membership.enums.SubscriptionStatus;
import com.firstclub.membership.enums.TierType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * The central aggregate for a user's membership subscription.
 *
 * One user has at most ONE active subscription at any time (enforced via unique constraint).
 * Version field enables optimistic locking to handle concurrent upgrade/downgrade/cancel races.
 */
@Entity
@Table(name = "user_subscriptions",
       indexes = {
           @Index(name = "idx_sub_user_id", columnList = "user_id"),
           @Index(name = "idx_sub_status", columnList = "status"),
           @Index(name = "idx_sub_expiry", columnList = "expiresAt")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plan_id", nullable = false)
    private MembershipPlan plan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TierType tierType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime cancelledAt;

    /** Optimistic locking — prevents lost-update in concurrent tier changes */
    @Version
    private Long version;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return status == SubscriptionStatus.ACTIVE && LocalDateTime.now().isBefore(expiresAt);
    }
}
