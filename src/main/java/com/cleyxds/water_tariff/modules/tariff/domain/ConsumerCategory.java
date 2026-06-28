package com.cleyxds.water_tariff.modules.tariff.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "consumer_categories",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_consumer_category_per_tariff",
                        columnNames = {"tariff_id", "code"}
                )
        }
)
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsumerCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 120)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tariff_id", nullable = false)
    private Tariff tariff;

    @Builder.Default
    @OneToMany(mappedBy = "consumerCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConsumptionRange> ranges = new ArrayList<>();
}
