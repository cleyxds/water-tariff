package com.cleyxds.water_tariff.modules.calculation.controller;

import com.cleyxds.water_tariff.modules.calculation.dto.CalculateTariffRequest;
import com.cleyxds.water_tariff.modules.calculation.dto.CalculateTariffResponse;
import com.cleyxds.water_tariff.modules.calculation.service.TariffCalculationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/calculos")
@RequiredArgsConstructor
public class TariffCalculationController {

    private final TariffCalculationService tariffCalculationService;

    @PostMapping
    public ResponseEntity<CalculateTariffResponse> calculate(@Valid @RequestBody CalculateTariffRequest request) {
        return ResponseEntity.ok(tariffCalculationService.calculate(request));
    }
}
