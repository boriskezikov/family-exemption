package ru.family.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.family.demo.dto.ResolveExemptionRequest;
import ru.family.demo.logic.ResolveExemptionOperation;
import ru.family.demo.model.Exemption;


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

