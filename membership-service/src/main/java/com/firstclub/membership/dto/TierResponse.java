package com.firstclub.membership.dto;

import com.firstclub.membership.enums.TierType;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data @Builder
public class TierResponse {
    private TierType tierType;
    private List<TierBenefitResponse> benefits;
}
