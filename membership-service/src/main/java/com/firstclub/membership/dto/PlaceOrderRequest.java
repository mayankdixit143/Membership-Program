package com.firstclub.membership.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PlaceOrderRequest {
    @NotNull @DecimalMin("0.01") private BigDecimal orderValue;
}
