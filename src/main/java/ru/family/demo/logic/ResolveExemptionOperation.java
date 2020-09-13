package ru.family.demo.logic;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;
import org.springframework.web.client.HttpStatusCodeException;
import ru.family.demo.dto.ResolveExemptionRequest;
import ru.family.demo.model.Exemption;
import ru.family.demo.model.UserExemption;
import ru.family.demo.service.ExemptionDao;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResolveExemptionOperation {

    private final ExemptionDao exemptionDao;
    private final Gson gson = new Gson();

    public List<Exemption> process(ResolveExemptionRequest worksheet) {
        try {
            return internalProcess(worksheet);
        } catch (HttpStatusCodeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw InternalServerError.create(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong", HttpHeaders.EMPTY, null, null);
        }
    }

    @Transactional
    public List<Exemption> internalProcess(ResolveExemptionRequest request) {
        var worksheet = request.worksheet();
        var criteria = exemptionDao.findCriteria(worksheet.keySet()).stream()
            .map(c -> Map.entry(c.name(), c))
            .collect(toMap(Entry::getKey, Entry::getValue));
        var filteredInput = worksheet.entrySet().stream()
            .filter(c -> criteria.containsKey(c.getKey()))
            .map(e -> {
                if (e.getValue() instanceof String s && s.matches("[/d]{4}-[/d]{2}-[/d]{2}")) {
                    return Map.entry(e.getKey(), LocalDate.parse(s));
                }
                return e;
            })
            .collect(toMap(Entry::getKey, Entry::getValue));
        var exemptions = exemptionDao.findExemption(filteredInput, criteria);

        if (!request.isDefaultUser()) {
            var worksheetJson = gson.toJson(request.worksheet());
            var userExemption = new UserExemption(
                request.userId(),
                exemptions.stream()
                    .map(Exemption::id)
                    .collect(toSet()),
                Set.of(worksheetJson)
            );
            exemptionDao.saveUserExemption(userExemption);
        }
        return exemptions;
    }
}
