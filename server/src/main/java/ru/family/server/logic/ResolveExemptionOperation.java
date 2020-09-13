package ru.family.server.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;
import org.springframework.web.client.HttpStatusCodeException;
import ru.family.server.model.Exemption;
import ru.family.server.service.ExemptionDao;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static java.util.stream.Collectors.toMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResolveExemptionOperation {

    private final ExemptionDao exemptionDao;

    public List<Exemption> process(Map<String, Object> worksheet) {
        try {
            return internalProcess(worksheet);
        } catch (HttpStatusCodeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw InternalServerError.create(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong", HttpHeaders.EMPTY, null, null);
        }
    }

    public List<Exemption> internalProcess(Map<String, Object> worksheet) {
        var criteria = exemptionDao.findCriteria(worksheet.keySet()).stream()
            .map(c -> Map.entry(c.name().toLowerCase(), c))
            .collect(toMap(Entry::getKey, Entry::getValue));
        var filteredInput = worksheet.entrySet().stream()
            .filter(c -> criteria.containsKey(c.getKey().toLowerCase()))
            .collect(toMap(Entry::getKey, Entry::getValue));
        return exemptionDao.findExemption(filteredInput, criteria);
    }
}
