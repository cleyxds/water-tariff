package com.cleyxds.water_tariff.modules.tariff.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record CreateTariffTableRequest(
        @NotBlank @Size(max = 120) String name,

        @NotNull LocalDate effectiveDate,

        @Valid @NotEmpty List<CreateConsumerCategoryRequest> categories) {
}
