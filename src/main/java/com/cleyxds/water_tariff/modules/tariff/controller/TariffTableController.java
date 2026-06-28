package com.cleyxds.water_tariff.modules.tariff.controller;

import com.cleyxds.water_tariff.modules.tariff.dto.CreateTariffTableRequest;
import com.cleyxds.water_tariff.modules.tariff.dto.TariffTableResponse;
import com.cleyxds.water_tariff.modules.tariff.service.TariffTableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tabelas-tarifarias")
@RequiredArgsConstructor
public class TariffTableController {

    private final TariffTableService tariffTableService;

    @PostMapping
    public ResponseEntity<TariffTableResponse> create(@Valid @RequestBody CreateTariffTableRequest request) {
        TariffTableResponse response = tariffTableService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public List<TariffTableResponse> list() {
        return tariffTableService.listActive();
    }

    @GetMapping("/{id}")
    public TariffTableResponse findById(@PathVariable UUID id) {
        return tariffTableService.findActiveById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disable(@PathVariable UUID id) {
        tariffTableService.disable(id);
    }
}
