package ru.family.demo.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public record Criteria(
    Long id,
    String name,
    Type type,
    String description
) {

    public static final RowMapper ROW_MAPPER = new RowMapper();

    public static class RowMapper implements org.springframework.jdbc.core.RowMapper<Criteria> {

        @Override
        public Criteria mapRow(ResultSet resultSet, int i) throws SQLException {
            return new Criteria(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                Type.valueOf(resultSet.getString("type").toUpperCase()),
                resultSet.getString("description")
            );
        }
    }

    public enum Type {
        STRING, INT, BOOLEAN
    }
}
