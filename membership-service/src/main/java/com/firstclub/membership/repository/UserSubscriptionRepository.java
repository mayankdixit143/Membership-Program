package com.firstclub.membership.repository;

import com.firstclub.membership.entity.UserSubscription;
import com.firstclub.membership.enums.SubscriptionStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {

    Optional<UserSubscription> findByUserIdAndStatus(Long userId, SubscriptionStatus status);

    /** Pessimistic write lock for concurrent mutation (upgrade/downgrade/cancel). */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM UserSubscription s WHERE s.id = :id")
    Optional<UserSubscription> findByIdWithLock(@Param("id") Long id);

    List<UserSubscription> findByStatusAndExpiresAtBefore(SubscriptionStatus status, LocalDateTime dateTime);

    boolean existsByUserIdAndStatus(Long userId, SubscriptionStatus status);
}
