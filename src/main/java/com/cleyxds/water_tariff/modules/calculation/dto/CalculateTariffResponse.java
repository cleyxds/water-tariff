package com.cleyxds.water_tariff.modules.calculation.dto;

import java.math.BigDecimal;
import java.util.List;

public record CalculateTariffResponse(
        String category,
        Integer totalConsumption,
        BigDecimal totalAmount,
        List<CalculationRangeDetailResponse> details) {
}
