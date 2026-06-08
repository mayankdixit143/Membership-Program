package com.firstclub.membership.repository;

import com.firstclub.membership.entity.UserOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface UserOrderRepository extends JpaRepository<UserOrder, Long> {

    @Query("SELECT COUNT(o) FROM UserOrder o WHERE o.user.id = :userId AND o.createdAt >= :since")
    long countOrdersSince(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    @Query("SELECT COALESCE(SUM(o.orderValue), 0) FROM UserOrder o WHERE o.user.id = :userId AND o.createdAt >= :since")
    BigDecimal sumOrderValueSince(@Param("userId") Long userId, @Param("since") LocalDateTime since);
}
