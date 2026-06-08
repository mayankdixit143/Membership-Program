package com.firstclub.membership.controller;

import com.firstclub.membership.dto.ApiResponse;
import com.firstclub.membership.dto.CreateUserRequest;
import com.firstclub.membership.dto.PlaceOrderRequest;
import com.firstclub.membership.entity.User;
import com.firstclub.membership.entity.UserOrder;
import com.firstclub.membership.enums.TierType;
import com.firstclub.membership.service.TierEvaluationService;
import com.firstclub.membership.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final TierEvaluationService tierEvaluationService;

    /** POST /api/v1/users — create a user */
    @PostMapping
    public ResponseEntity<ApiResponse<User>> createUser(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("User created", userService.createUser(request)));
    }

    /** GET /api/v1/users/{userId} */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<User>> getUser(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getUser(userId)));
    }

    /** POST /api/v1/users/{userId}/orders — simulate placing an order (updates tier eligibility) */
    @PostMapping("/{userId}/orders")
    public ResponseEntity<ApiResponse<UserOrder>> placeOrder(
            @PathVariable Long userId,
            @Valid @RequestBody PlaceOrderRequest request) {
        UserOrder order = userService.placeOrder(userId, request.getOrderValue());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Order placed", order));
    }

    /** GET /api/v1/users/{userId}/eligible-tier — compute tier the user qualifies for */
    @GetMapping("/{userId}/eligible-tier")
    public ResponseEntity<ApiResponse<TierType>> getEligibleTier(@PathVariable Long userId) {
        User user = userService.getUser(userId);
        TierType tier = tierEvaluationService.evaluateTierForUser(user);
        return ResponseEntity.ok(ApiResponse.ok("Eligible tier computed", tier));
    }
}
