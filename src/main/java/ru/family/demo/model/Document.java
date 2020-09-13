package ru.family.demo.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public record Document(
    Long id,
    String name,
    byte[] data
) {

    public static final RowMapper ROW_MAPPER = new RowMapper();

    public static class RowMapper implements org.springframework.jdbc.core.RowMapper<Document> {

        @Override
        public Document mapRow(ResultSet resultSet, int i) throws SQLException {
            return new Document(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getBytes("data")
            );
        }
    }
}
