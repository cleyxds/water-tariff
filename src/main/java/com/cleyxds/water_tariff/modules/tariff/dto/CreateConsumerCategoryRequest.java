package com.cleyxds.water_tariff.modules.tariff.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateConsumerCategoryRequest(
        @NotBlank @Size(max = 50) String code,

        @NotBlank @Size(max = 120) String name,

        @Valid @NotEmpty List<CreateConsumptionRangeRequest> ranges) {
}
