package com.cleyxds.water_tariff.modules.tariff.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record TariffTableResponse(
        UUID id,
        String name,
        LocalDate effectiveDate,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<ConsumerCategoryResponse> categories) {
}
