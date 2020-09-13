package ru.family.demo.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public record UserExemption(
    Long userId,
    List<Long> exemptionIds,
    String worksheet
) {

    public static final RowMapper ROW_MAPPER = new RowMapper();

    public Map<String, ?> asArgs() {
        return Map.of(
            "userId", userId,
            "worksheet", worksheet
        );
    }

    public static class RowMapper implements org.springframework.jdbc.core.RowMapper<UserExemption> {

        @Override
        public UserExemption mapRow(ResultSet resultSet, int i) throws SQLException {
            return new UserExemption(
                resultSet.getLong("user_id"),
                List.of((Long[]) resultSet.getArray("exemption_ids").getArray()),
                resultSet.getString("worksheet")
            );
        }
    }
}
