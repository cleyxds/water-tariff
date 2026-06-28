package com.cleyxds.water_tariff.modules.tariff.service;

import com.cleyxds.water_tariff.modules.tariff.domain.ConsumerCategory;
import com.cleyxds.water_tariff.modules.tariff.domain.ConsumptionRange;
import com.cleyxds.water_tariff.modules.tariff.domain.TariffTable;
import com.cleyxds.water_tariff.modules.tariff.dto.ConsumerCategoryResponse;
import com.cleyxds.water_tariff.modules.tariff.dto.ConsumptionRangeResponse;
import com.cleyxds.water_tariff.modules.tariff.dto.CreateConsumerCategoryRequest;
import com.cleyxds.water_tariff.modules.tariff.dto.CreateConsumptionRangeRequest;
import com.cleyxds.water_tariff.modules.tariff.dto.CreateTariffTableRequest;
import com.cleyxds.water_tariff.modules.tariff.dto.TariffTableResponse;
import com.cleyxds.water_tariff.modules.tariff.repository.TariffTableRepository;
import com.cleyxds.water_tariff.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TariffTableService {

    private final TariffTableRepository tariffTableRepository;
    private final TariffTableRangeValidator tariffTableRangeValidator;

    @Transactional
    public TariffTableResponse create(CreateTariffTableRequest request) {
        tariffTableRangeValidator.validate(request.categories());

        TariffTable tariffTable = TariffTable.builder()
                .name(request.name().trim())
                .effectiveDate(request.effectiveDate())
                .active(true)
                .build();

        tariffTable.setCategories(mapCategories(request.categories(), tariffTable));

        return toResponse(tariffTableRepository.save(tariffTable));
    }

    @Transactional(readOnly = true)
    public List<TariffTableResponse> listActive() {
        return tariffTableRepository.findAllByActiveTrueOrderByEffectiveDateDescCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TariffTableResponse findActiveById(UUID id) {
        return tariffTableRepository.findByIdAndActiveTrue(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Tabela tarifaria nao encontrada"));
    }

    @Transactional
    public void disable(UUID id) {
        TariffTable tariffTable = tariffTableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tabela tarifaria nao encontrada"));

        tariffTable.setActive(false);
    }

    private List<ConsumerCategory> mapCategories(List<CreateConsumerCategoryRequest> requests, TariffTable tariffTable) {
        return requests.stream()
                .map(request -> {
                    ConsumerCategory category = ConsumerCategory.builder()
                            .code(request.code().trim().toUpperCase())
                            .name(request.name().trim())
                            .tariffTable(tariffTable)
                            .build();

                    category.setRanges(mapRanges(request.ranges(), category));

                    return category;
                })
                .toList();
    }

    private List<ConsumptionRange> mapRanges(List<CreateConsumptionRangeRequest> requests, ConsumerCategory category) {
        return requests.stream()
                .sorted(Comparator.comparing(CreateConsumptionRangeRequest::startM3))
                .map(request -> ConsumptionRange.builder()
                        .startM3(request.startM3())
                        .endM3(request.endM3())
                        .unitPrice(request.unitPrice())
                        .consumerCategory(category)
                        .build())
                .toList();
    }

    private TariffTableResponse toResponse(TariffTable tariffTable) {
        return new TariffTableResponse(
                tariffTable.getId(),
                tariffTable.getName(),
                tariffTable.getEffectiveDate(),
                tariffTable.getActive(),
                tariffTable.getCreatedAt(),
                tariffTable.getUpdatedAt(),
                tariffTable.getCategories()
                        .stream()
                        .map(this::toResponse)
                        .toList());
    }

    private ConsumerCategoryResponse toResponse(ConsumerCategory category) {
        return new ConsumerCategoryResponse(
                category.getId(),
                category.getCode(),
                category.getName(),
                category.getRanges()
                        .stream()
                        .sorted(Comparator.comparing(ConsumptionRange::getStartM3))
                        .map(this::toResponse)
                        .toList());
    }

    private ConsumptionRangeResponse toResponse(ConsumptionRange range) {
        return new ConsumptionRangeResponse(
                range.getId(),
                range.getStartM3(),
                range.getEndM3(),
                range.getUnitPrice());
    }
}
