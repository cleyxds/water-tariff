package com.cleyxds.water_tariff.modules.tariff.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ConsumptionRangeResponse(
        UUID id,
        Integer startM3,
        Integer endM3,
        BigDecimal unitPrice) {
}
