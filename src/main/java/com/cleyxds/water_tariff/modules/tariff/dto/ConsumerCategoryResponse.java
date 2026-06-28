package com.cleyxds.water_tariff.modules.tariff.dto;

import java.util.List;
import java.util.UUID;

public record ConsumerCategoryResponse(
        UUID id,
        String code,
        String name,
        List<ConsumptionRangeResponse> ranges) {
}
