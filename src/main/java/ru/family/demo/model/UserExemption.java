package ru.family.demo.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

public record UserExemption(
    Long userId,
    Set<Long> exemptionIds,
    Set<String> worksheets
) {

    public static final RowMapper ROW_MAPPER = new RowMapper();

    public Map<String, ?> asArgs() {
        return Map.of(
            "userId", userId,
            "exemptionIds", exemptionIds.stream().map(String::valueOf).collect(joining(" ,", "{", "}")),
            "worksheet", worksheets.toArray()[0]
        );
    }

    public static class RowMapper implements org.springframework.jdbc.core.RowMapper<UserExemption> {

        @Override
        public UserExemption mapRow(ResultSet resultSet, int i) throws SQLException {
            return new UserExemption(
                resultSet.getLong("user_id"),
                Set.of((Long[]) resultSet.getArray("exemption_ids").getArray()),
                Set.of(resultSet.getString("worksheet"))
            );
        }
    }

    public static UserExemption merge(List<UserExemption> userExemption) {
        var userId = userExemption.get(0).userId;
        var worksheets = userExemption.stream()
            .map(UserExemption::worksheets)
            .flatMap(Collection::stream)
            .collect(toSet());
        var exemptionIds = userExemption.stream()
            .map(UserExemption::exemptionIds)
            .flatMap(Collection::stream)
            .collect(toSet());
        return new UserExemption(userId, exemptionIds, worksheets);
    }
}
