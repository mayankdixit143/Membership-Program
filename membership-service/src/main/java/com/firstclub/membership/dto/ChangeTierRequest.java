package com.firstclub.membership.dto;

import com.firstclub.membership.enums.TierType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeTierRequest {
    @NotNull private TierType newTier;
}
