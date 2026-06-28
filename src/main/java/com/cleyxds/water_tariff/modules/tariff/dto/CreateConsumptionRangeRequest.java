package com.cleyxds.water_tariff.modules.tariff.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateConsumptionRangeRequest(
        @NotNull @Min(0) Integer startM3,

        @NotNull @Min(0) Integer endM3,

        @NotNull @DecimalMin("0.00") BigDecimal unitPrice) {
}
