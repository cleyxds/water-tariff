package com.cleyxds.water_tariff.modules.tariff.repository;

import com.cleyxds.water_tariff.modules.tariff.domain.ConsumerCategory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ConsumerCategoryRepository extends JpaRepository<ConsumerCategory, UUID> {

    @EntityGraph(attributePaths = {"tariffTable", "ranges"})
    List<ConsumerCategory> findByCodeIgnoreCaseAndTariffTableActiveTrueOrderByTariffTableEffectiveDateDescTariffTableCreatedAtDesc(
            String code
    );
}
