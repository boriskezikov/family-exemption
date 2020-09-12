package ru.family.server.model;

import java.time.LocalDateTime;
import java.util.List;

public record Exemption(
    Long id,
    String name,
    String description,
    List<Long> documentReferences,
    LocalDateTime startPeriod,
    LocalDateTime endPeriod,
    LocalDateTime created
) {
}
