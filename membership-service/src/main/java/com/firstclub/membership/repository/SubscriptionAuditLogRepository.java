package com.firstclub.membership.repository;

import com.firstclub.membership.entity.SubscriptionAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionAuditLogRepository extends JpaRepository<SubscriptionAuditLog, Long> {
    List<SubscriptionAuditLog> findBySubscriptionIdOrderByCreatedAtDesc(Long subscriptionId);
}
