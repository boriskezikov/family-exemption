package ru.family.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.family.server.dto.ResolveExemptionRequest;
import ru.family.server.logic.ResolveExemptionOperation;
import ru.family.server.model.Exemption;

import java.util.List;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/exemption")
@RequiredArgsConstructor
public class RestController {

    private final ResolveExemptionOperation resolveExemptionOperation;

    @PostMapping("/")
    public List<Exemption> resolveExemption(@RequestBody ResolveExemptionRequest worksheet) {
        return resolveExemptionOperation.process(worksheet);
    }
}

