package com.firstclub.membership.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a user order.  Used by TierEvaluationService to compute
 * monthly order counts and values for tier upgrades/downgrades.
 */
@Entity
@Table(name = "user_orders",
       indexes = {
           @Index(name = "idx_user_orders_user_id", columnList = "user_id"),
           @Index(name = "idx_user_orders_created_at", columnList = "createdAt")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal orderValue;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
