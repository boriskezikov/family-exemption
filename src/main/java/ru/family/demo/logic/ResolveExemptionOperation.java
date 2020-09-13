package ru.family.server.logic;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;
import org.springframework.web.client.HttpStatusCodeException;
import ru.family.server.dto.ResolveExemptionRequest;
import ru.family.server.model.Exemption;
import ru.family.server.model.UserExemption;
import ru.family.server.service.ExemptionDao;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

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
            .map(c -> Map.entry(c.name().toLowerCase(), c))
            .collect(toMap(Entry::getKey, Entry::getValue));
        var filteredInput = worksheet.entrySet().stream()
            .filter(c -> criteria.containsKey(c.getKey().toLowerCase()))
            .collect(toMap(Entry::getKey, Entry::getValue));
        var exemptions = exemptionDao.findExemption(filteredInput, criteria);

        if (!request.isDefaultUser()) {
            var worksheetJson = gson.toJson(request.worksheet());
            var userExemption = new UserExemption(
                request.userId(),
                exemptions.stream()
                    .map(Exemption::id)
                    .collect(toList()),
                worksheetJson
            );
            exemptionDao.saveUserExemption(userExemption);
        }
        return exemptions;
    }
}
