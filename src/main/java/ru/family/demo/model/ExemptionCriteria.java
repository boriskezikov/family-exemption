package ru.family.demo.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExemptionCriteria(
    Long id,
    Long exemptionId,
    Long criteriaId,
    Integer intValue,
    String stringValue,
    Boolean booleanValue,
    LocalDate dateValue,
    LocalDateTime startPeriod,
    LocalDateTime endPeriod,
    LocalDateTime created
) {
}
