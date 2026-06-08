package com.firstclub.membership.dto;

import com.firstclub.membership.enums.PlanType;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data @Builder
public class PlanResponse {
    private Long id;
    private PlanType planType;
    private BigDecimal price;
    private String description;
    private String promoLabel;
    private int durationMonths;
}
