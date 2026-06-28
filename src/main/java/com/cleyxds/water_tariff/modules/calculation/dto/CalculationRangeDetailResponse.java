package com.cleyxds.water_tariff.modules.calculation.dto;

import java.math.BigDecimal;

public record CalculationRangeDetailResponse(
        CalculationRangeResponse faixa,
        Integer m3Cobrados,
        BigDecimal valorUnitario,
        BigDecimal subtotal) {
}
