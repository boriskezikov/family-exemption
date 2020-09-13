package ru.family.server.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public record ExemptionCriteria(
    Long id,
    String name,
    String description,
    List<Long> documentReference,
    LocalDateTime startPeriod,
    LocalDateTime endPeriod,
    LocalDateTime created
) {

    public static final RowMapper ROW_MAPPER = new RowMapper();

    public static class RowMapper implements org.springframework.jdbc.core.RowMapper<ExemptionCriteria> {

        @Override
        public ExemptionCriteria mapRow(ResultSet resultSet, int i) throws SQLException {
            return new ExemptionCriteria(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                List.of((Long[]) resultSet.getArray("reference_document").getArray()),
                resultSet.getTimestamp("start_period").toLocalDateTime(),
                resultSet.getTimestamp("end_period").toLocalDateTime(),
                resultSet.getTimestamp("end_period").toLocalDateTime()
            );
        }
    }
}
