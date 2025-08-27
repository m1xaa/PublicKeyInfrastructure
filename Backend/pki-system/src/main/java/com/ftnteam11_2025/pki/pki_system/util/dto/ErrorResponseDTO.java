package com.ftnteam11_2025.pki.pki_system.util.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Data
@RequiredArgsConstructor
public class ErrorResponseDTO {
    private final Integer code;
    private final String message;
    private final Map<String, String> errors;

    public ErrorResponseDTO(Integer code, String message) {
        this.code = code;
        this.message = message;
        this.errors = new HashMap<>();
    }


    public Map<String, String> getErrors() {
        return Collections.unmodifiableMap(errors);
    }

    public void addError(String key, String message) {
        errors.put(key, message);
    }
}
