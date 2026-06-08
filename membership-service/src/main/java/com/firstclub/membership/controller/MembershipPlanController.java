package com.firstclub.membership.controller;

import com.firstclub.membership.dto.ApiResponse;
import com.firstclub.membership.dto.PlanResponse;
import com.firstclub.membership.dto.TierResponse;
import com.firstclub.membership.service.MembershipPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Exposes membership plans and tier benefits for the user to browse before subscribing.
 */
@RestController
@RequestMapping("/api/v1/membership")
@RequiredArgsConstructor
public class MembershipPlanController {

    private final MembershipPlanService planService;

    /** GET /api/v1/membership/plans — list all active plans */
    @GetMapping("/plans")
    public ResponseEntity<ApiResponse<List<PlanResponse>>> getPlans() {
        return ResponseEntity.ok(ApiResponse.ok(planService.getAllActivePlans()));
    }

    /** GET /api/v1/membership/plans/{planId} — get a single plan */
    @GetMapping("/plans/{planId}")
    public ResponseEntity<ApiResponse<PlanResponse>> getPlan(@PathVariable Long planId) {
        return ResponseEntity.ok(ApiResponse.ok(planService.getPlanById(planId)));
    }

    /** GET /api/v1/membership/tiers — list all tiers with their benefits */
    @GetMapping("/tiers")
    public ResponseEntity<ApiResponse<List<TierResponse>>> getTiers() {
        return ResponseEntity.ok(ApiResponse.ok(planService.getAllTiersWithBenefits()));
    }
}
