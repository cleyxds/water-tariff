package com.cleyxds.water_tariff.modules.calculation.service;

import com.cleyxds.water_tariff.modules.calculation.dto.CalculateTariffRequest;
import com.cleyxds.water_tariff.modules.calculation.dto.CalculateTariffResponse;
import com.cleyxds.water_tariff.modules.calculation.dto.CalculationRangeDetailResponse;
import com.cleyxds.water_tariff.modules.calculation.dto.CalculationRangeResponse;
import com.cleyxds.water_tariff.modules.tariff.domain.ConsumerCategory;
import com.cleyxds.water_tariff.modules.tariff.domain.ConsumptionRange;
import com.cleyxds.water_tariff.modules.tariff.repository.ConsumerCategoryRepository;
import com.cleyxds.water_tariff.shared.exception.BusinessException;
import com.cleyxds.water_tariff.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TariffCalculationService {

    private final ConsumerCategoryRepository consumerCategoryRepository;

    @Transactional(readOnly = true)
    public CalculateTariffResponse calculate(CalculateTariffRequest request) {
        ConsumerCategory category = consumerCategoryRepository
                .findByCodeIgnoreCaseAndTariffTableActiveTrueOrderByTariffTableEffectiveDateDescTariffTableCreatedAtDesc(
                        request.category().trim())
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Categoria tarifaria ativa nao encontrada"));

        List<ConsumptionRange> ranges = category.getRanges()
                .stream()
                .sorted(Comparator.comparing(ConsumptionRange::getStartM3))
                .toList();

        validateConsumptionCoverage(request.consumption(), ranges);

        List<CalculationRangeDetailResponse> details = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (ConsumptionRange range : ranges) {
            if (request.consumption() <= range.getStartM3() && range.getStartM3() > 0) {
                break;
            }

            int chargedM3 = calculateChargedM3(request.consumption(), range);

            if (chargedM3 <= 0) {
                continue;
            }

            BigDecimal subtotal = range.getUnitPrice().multiply(BigDecimal.valueOf(chargedM3));
            total = total.add(subtotal);

            details.add(new CalculationRangeDetailResponse(
                    new CalculationRangeResponse(range.getStartM3(), range.getEndM3()),
                    chargedM3,
                    range.getUnitPrice(),
                    subtotal));
        }

        return new CalculateTariffResponse(
                category.getCode(),
                request.consumption(),
                total,
                details);
    }

    private void validateConsumptionCoverage(Integer consumption, List<ConsumptionRange> ranges) {
        if (ranges.isEmpty()) {
            throw new BusinessException("Categoria tarifaria nao possui faixas de consumo cadastradas");
        }

        ConsumptionRange lastRange = ranges.getLast();

        if (consumption > lastRange.getEndM3()) {
            throw new BusinessException("Consumo informado excede a ultima faixa cadastrada para a categoria");
        }
    }

    private int calculateChargedM3(Integer consumption, ConsumptionRange range) {
        int upperLimit = Math.min(consumption, range.getEndM3());
        int lowerLimit = range.getStartM3() == 0 ? 0 : range.getStartM3() - 1;

        return upperLimit - lowerLimit;
    }
}
