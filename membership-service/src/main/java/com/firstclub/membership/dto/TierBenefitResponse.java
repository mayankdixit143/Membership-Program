package com.firstclub.membership.dto;

import com.firstclub.membership.enums.BenefitType;
import com.firstclub.membership.enums.TierType;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class TierBenefitResponse {
    private TierType tierType;
    private BenefitType benefitType;
    private String benefitValue;
    private String description;
}
