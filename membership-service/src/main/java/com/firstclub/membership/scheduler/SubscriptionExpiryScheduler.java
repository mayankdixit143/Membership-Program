package com.firstclub.membership.scheduler;

import com.firstclub.membership.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled tasks for membership lifecycle management.
 *
 * Runs every hour to expire subscriptions whose expiresAt has passed.
 * In a distributed setup this should be backed by a distributed lock (e.g. ShedLock)
 * to prevent duplicate execution across multiple instances.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SubscriptionExpiryScheduler {

    private final SubscriptionService subscriptionService;

    @Scheduled(fixedRateString = "${membership.expiry-check-interval-ms:3600000}")
    public void expireStaleSubscriptions() {
        log.info("Running subscription expiry check...");
        int expired = subscriptionService.expireSubscriptions();
        log.info("Subscription expiry check complete. Expired: {}", expired);
    }
}
