package ru.family.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;
import ru.family.demo.model.Criteria;
import ru.family.demo.model.Exemption;
import ru.family.demo.model.UserExemption;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

@Component
@RequiredArgsConstructor
public class ExemptionDao {

    public final NamedParameterJdbcTemplate jdbcTemplate;

    public List<Exemption> findExemption(Map<String, Object> search, Map<String, Criteria> criteria) {
        var and = new StringJoiner(" AND ");
        var join = new StringJoiner(" LEFT JOIN ")
            .add("SELECT e.* FROM exemption AS e ");
        var args = new HashMap<String, Object>();

        for (var s : search.entrySet()) {
            var name = s.getKey().toLowerCase();
            join.add(format("exemption_criteria AS %s ON e.id = %s.exemption_id and %s.criteria_id = :%s",
                            name, name, name, name.concat("_id")));
            args.put(name, s.getValue());
            if (s.getValue() instanceof Boolean) {
                and.add(format("%s.boolean_value = :%s", name, name));
            } else if (s.getValue() instanceof Integer) {
                and.add(format("%s.int_value = :%s", name, name));
            } else if (s.getValue() instanceof String) {
                and.add(format("%s.string_value = :%s", name, name));
            }
            args.put(name.concat("_id"), criteria.get(name).id());
        }
        var sql = join.toString()
            .concat(" WHERE ")
            .concat(and.toString());

        return doRequest(() -> jdbcTemplate.query(sql, args, Exemption.ROW_MAPPER));
    }

    public List<Criteria> findCriteria(Collection<String> names) {
        var args = names.stream()
            .map(s -> "'".concat(s).concat("'"))
            .collect(joining(", ", "(", ")"));
        return doRequest(() -> jdbcTemplate.query(
            format("SELECT * FROM public.criteria WHERE upper(name) IN %s", args), Map.of(),
            Criteria.ROW_MAPPER));
    }

    public void saveUserExemption(UserExemption userExemption) {
        //fixme add exemption ids field
        doRequest(() -> jdbcTemplate.update(
            "INSERT INTO public.user_exemption (user_id, worksheet) "
                + "VALUES (:userId, :worksheet)", userExemption.asArgs())
        );
    }

    private <R> R doRequest(Supplier<R> getDataCallback) {
        try {
            return getDataCallback.get();
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            throw InternalServerError.create(
                HttpStatus.INTERNAL_SERVER_ERROR, Objects.requireNonNull(e.getMessage(), "Data access exception"),
                HttpHeaders.EMPTY, null, null);
        }
    }
}
