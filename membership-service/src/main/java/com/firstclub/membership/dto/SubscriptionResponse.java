package com.firstclub.membership.dto;

import com.firstclub.membership.enums.PlanType;
import com.firstclub.membership.enums.SubscriptionStatus;
import com.firstclub.membership.enums.TierType;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder
public class SubscriptionResponse {
    private Long subscriptionId;
    private Long userId;
    private PlanType planType;
    private BigDecimal planPrice;
    private TierType tierType;
    private SubscriptionStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime expiresAt;
    private LocalDateTime cancelledAt;
    private List<TierBenefitResponse> currentBenefits;
    private boolean active;
}
