package com.cleyxds.water_tariff.modules.calculation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CalculateTariffRequest(
        @NotBlank String categoria,

        @NotNull @Min(0) Integer consumo) {
}
