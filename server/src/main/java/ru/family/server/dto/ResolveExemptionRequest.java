package ru.family.server.dto;

import java.util.Map;

public record ResolveExemptionRequest(
    Map<String, Object> worksheet,
    Long userId
) {

    public boolean isDefaultUser() {
        return userId == 1L;
    }
}
