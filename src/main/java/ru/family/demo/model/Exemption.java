package ru.family.server.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Objects.nonNull;

public record Exemption(
    Long id,
    String name,
    String description,
    List<Long> documentReferences,
    LocalDateTime startPeriod,
    LocalDateTime endPeriod,
    LocalDateTime created
) {

    public static final RowMapper ROW_MAPPER = new RowMapper();

    public static class RowMapper implements org.springframework.jdbc.core.RowMapper<Exemption> {

        @Override
        public Exemption mapRow(ResultSet resultSet, int i) throws SQLException {
            return new Exemption(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                List.of((Long[]) resultSet.getArray("reference_document").getArray()),
                nonNull(resultSet.getTimestamp("start_period"))
                ? resultSet.getTimestamp("start_period").toLocalDateTime() : null,
                nonNull(resultSet.getTimestamp("end_period"))
                ? resultSet.getTimestamp("end_period").toLocalDateTime() : null,
                nonNull(resultSet.getTimestamp("created"))
                ? resultSet.getTimestamp("created").toLocalDateTime() : null
            );
        }
    }
}
