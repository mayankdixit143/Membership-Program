package com.firstclub.membership.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a platform user.
 * cohortTags are used by USER_COHORT tier criteria.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    /**
     * Arbitrary cohort tags assigned to this user (e.g. "vip", "beta_tester").
     * Used for USER_COHORT tier criteria evaluation.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_cohort_tags", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "cohort_tag")
    @Builder.Default
    private Set<String> cohortTags = new HashSet<>();

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
