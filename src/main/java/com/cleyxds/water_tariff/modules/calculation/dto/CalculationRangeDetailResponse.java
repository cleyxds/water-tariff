package com.cleyxds.water_tariff.modules.calculation.dto;

import java.math.BigDecimal;

public record CalculationRangeDetailResponse(
        CalculationRangeResponse range,
        Integer chargedM3,
        BigDecimal unitPrice,
        BigDecimal subtotal) {
}
