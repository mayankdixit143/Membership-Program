package com.firstclub.membership.controller;

import com.firstclub.membership.dto.*;
import com.firstclub.membership.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Subscription lifecycle: subscribe, upgrade, downgrade, cancel, track.
 */
@RestController
@RequestMapping("/api/v1/users/{userId}/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    /** POST /api/v1/users/{userId}/subscription — subscribe to a plan+tier */
    @PostMapping
    public ResponseEntity<ApiResponse<SubscriptionResponse>> subscribe(
            @PathVariable Long userId,
            @Valid @RequestBody SubscribeRequest request) {
        SubscriptionResponse response = subscriptionService.subscribe(userId, request.getPlanType(), request.getTierType());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Subscription created successfully", response));
    }

    /** GET /api/v1/users/{userId}/subscription — current active subscription */
    @GetMapping
    public ResponseEntity<ApiResponse<SubscriptionResponse>> getSubscription(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(subscriptionService.getCurrentSubscription(userId)));
    }

    /** PATCH /api/v1/users/{userId}/subscription/upgrade — upgrade tier */
    @PatchMapping("/upgrade")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> upgradeTier(
            @PathVariable Long userId,
            @Valid @RequestBody ChangeTierRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Tier upgraded", subscriptionService.upgradeTier(userId, request.getNewTier())));
    }

    /** PATCH /api/v1/users/{userId}/subscription/downgrade — downgrade tier */
    @PatchMapping("/downgrade")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> downgradeTier(
            @PathVariable Long userId,
            @Valid @RequestBody ChangeTierRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Tier downgraded", subscriptionService.downgradeTier(userId, request.getNewTier())));
    }

    /** DELETE /api/v1/users/{userId}/subscription — cancel subscription */
    @DeleteMapping
    public ResponseEntity<ApiResponse<SubscriptionResponse>> cancel(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.ok("Subscription cancelled", subscriptionService.cancelSubscription(userId)));
    }

    /** GET /api/v1/users/{userId}/subscription/{subscriptionId}/history */
    @GetMapping("/{subscriptionId}/history")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> history(
            @PathVariable Long userId,
            @PathVariable Long subscriptionId) {
        return ResponseEntity.ok(ApiResponse.ok(subscriptionService.getSubscriptionHistory(subscriptionId)));
    }
}
