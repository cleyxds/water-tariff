package com.cleyxds.water_tariff.modules.tariff.service;

import com.cleyxds.water_tariff.modules.tariff.dto.CreateConsumerCategoryRequest;
import com.cleyxds.water_tariff.modules.tariff.dto.CreateConsumptionRangeRequest;
import com.cleyxds.water_tariff.shared.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class TariffTableRangeValidator {

    private static final int MAX_SUPPORTED_CONSUMPTION_M3 = 99999;

    public void validate(List<CreateConsumerCategoryRequest> categories) {
        Set<String> categoryCodes = new HashSet<>();

        for (CreateConsumerCategoryRequest category : categories) {
            String normalizedCode = category.code().trim().toUpperCase();

            if (!categoryCodes.add(normalizedCode)) {
                throw new BusinessException("Categoria duplicada na tabela tarifaria: " + normalizedCode);
            }

            validateCategoryRanges(normalizedCode, category.ranges());
        }
    }

    private void validateCategoryRanges(String categoryCode, List<CreateConsumptionRangeRequest> ranges) {
        List<CreateConsumptionRangeRequest> sortedRanges = ranges.stream()
                .sorted(Comparator.comparing(CreateConsumptionRangeRequest::startM3))
                .toList();

        if (!sortedRanges.getFirst().startM3().equals(0)) {
            throw new BusinessException("A primeira faixa da categoria " + categoryCode + " deve iniciar em 0");
        }

        int expectedStart = 0;

        for (CreateConsumptionRangeRequest range : sortedRanges) {
            if (range.startM3() > range.endM3()) {
                throw new BusinessException(
                        "O inicio da faixa deve ser menor ou igual ao fim na categoria " + categoryCode);
            }

            if (!range.startM3().equals(expectedStart)) {
                throw new BusinessException(
                        "As faixas da categoria " + categoryCode + " devem ser continuas e sem sobreposicao");
            }

            expectedStart = range.endM3() + 1;
        }

        CreateConsumptionRangeRequest lastRange = sortedRanges.getLast();

        if (lastRange.endM3() < MAX_SUPPORTED_CONSUMPTION_M3) {
            throw new BusinessException(
                    "A ultima faixa da categoria " + categoryCode + " deve cobrir consumo ate "
                            + MAX_SUPPORTED_CONSUMPTION_M3 + " m3");
        }
    }
}
