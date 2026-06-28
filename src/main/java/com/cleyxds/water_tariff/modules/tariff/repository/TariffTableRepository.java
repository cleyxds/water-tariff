package com.cleyxds.water_tariff.modules.tariff.repository;

import com.cleyxds.water_tariff.modules.tariff.domain.TariffTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TariffTableRepository extends JpaRepository<TariffTable, UUID> {

    List<TariffTable> findAllByActiveTrueOrderByEffectiveDateDescCreatedAtDesc();

    Optional<TariffTable> findByIdAndActiveTrue(UUID id);
}
