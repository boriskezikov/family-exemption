package ru.family.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException.BadRequest;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;
import ru.family.demo.model.Criteria;
import ru.family.demo.model.Document;
import ru.family.demo.model.Exemption;
import ru.family.demo.model.UserExemption;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.joining;

@Component
@RequiredArgsConstructor
public class ExemptionDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<Exemption> findExemption(Map<String, ?> search, Map<String, Criteria> criteria) {
        var and = new StringJoiner(" AND ");
        var join = new StringJoiner(" LEFT JOIN ")
            .add("SELECT e.* FROM exemption AS e ");
        var args = new HashMap<String, Object>();
        var counter = 1;

        for (var s : search.entrySet()) {
            var alias = "criteria" + counter;
            var tableName = "table" + counter++;
            join.add(format("exemption_criteria AS %s ON e.id = %s.exemption_id and %s.criteria_id = :%s",
                            tableName, tableName, tableName, tableName.concat("_id")));
            args.put(tableName.concat("_id"), criteria.get(s.getKey()).id());
            if (s.getValue() instanceof Boolean) {
                and.add(format("%s.boolean_value = :%s", tableName, alias));
            } else if (s.getValue() instanceof Integer) {
                and.add(format("%s.int_value = :%s", tableName, alias));
            } else if (s.getValue() instanceof String) {
                and.add(format("%s.string_value = :%s", tableName, alias));
            } else if (s.getValue() instanceof LocalDate) {
                and.add(format("%s.date_value = :%s::date", tableName, alias));
            }
            args.put(alias, s.getValue());
        }
        var sql = join.toString()
            .concat(" WHERE ")
            .concat(and.toString());

        return doRequest(() -> jdbcTemplate.query(sql, args, Exemption.ROW_MAPPER));
    }

    public List<Exemption> getAllExemptions() {
        return doRequest(() -> jdbcTemplate.query("SELECT * FROM public.exemption", Map.of(), Exemption.ROW_MAPPER));
    }

    public Exemption getExemptionById(Long id) {
        if (isNull(id)) {
            throw BadRequest.create(HttpStatus.BAD_REQUEST, "id must not be null", HttpHeaders.EMPTY, null, null);
        }
        return doRequest(() -> jdbcTemplate.queryForObject("SELECT * FROM public.exemption WHERE id = :id", Map.of("id", id), Exemption.ROW_MAPPER));
    }

    public Document getDocumentById(Long id) {
        if (isNull(id)) {
            throw BadRequest.create(HttpStatus.BAD_REQUEST, "id must not be null", HttpHeaders.EMPTY, null, null);
        }
        return doRequest(() -> jdbcTemplate.queryForObject("SELECT * FROM public.document WHERE id = :id", Map.of("id", id), Document.ROW_MAPPER));
    }

    public List<Document> getAllDocuments() {
        return doRequest(() -> jdbcTemplate.query("SELECT * FROM public.document", Map.of(), Document.ROW_MAPPER));
    }

    public List<UserExemption> getUserExemptionsById(Long id) {
        if (isNull(id)) {
            throw BadRequest.create(HttpStatus.BAD_REQUEST, "id must not be null", HttpHeaders.EMPTY, null, null);
        }
        return doRequest(() -> jdbcTemplate.query("SELECT * FROM public.user_exemption WHERE user_id = :id", Map.of("id", id),
                                                           UserExemption.ROW_MAPPER));
    }

    public List<Criteria> findCriteria(Collection<String> names) {
        var args = names.stream()
            .map(s -> "'".concat(s).concat("'"))
            .collect(joining(", ", "(", ")"));
        return doRequest(() -> jdbcTemplate.query(
            format("SELECT * FROM public.criteria WHERE name IN %s", args), Map.of(),
            Criteria.ROW_MAPPER));
    }

    public void saveUserExemption(UserExemption userExemption) {
        doRequest(() -> jdbcTemplate.update(
            "INSERT INTO public.user_exemption (user_id, exemption_ids, worksheet) "
                + "VALUES (:userId, :exemptionIds::bigint[], :worksheet) "
                + "ON CONFLICT ON CONSTRAINT unique_user_data DO NOTHING", userExemption.asArgs())
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
