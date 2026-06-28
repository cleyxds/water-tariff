package com.cleyxds.water_tariff.shared.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ApiErrorResponse(
        LocalDateTime timestamp,
        Integer status,
        String error,
        List<String> messages
) {
}
