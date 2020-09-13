package ru.family.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.family.demo.dto.ResolveExemptionRequest;
import ru.family.demo.logic.ResolveExemptionOperation;
import ru.family.demo.model.Exemption;
import ru.family.demo.model.UserExemption;
import ru.family.demo.service.ExemptionDao;

import java.util.List;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class RestController {

    private final ResolveExemptionOperation resolveExemptionOperation;
    private final ExemptionDao exemptionDao;

    @PostMapping("exemption/")
    public List<Exemption> resolveExemption(@RequestBody ResolveExemptionRequest worksheet) {
        return resolveExemptionOperation.process(worksheet);
    }

    @GetMapping("exemption/")
    public List<Exemption> getAllExemptions() {
        return exemptionDao.getAllExemptions();
    }

    @GetMapping("exemption/{id}")
    public Exemption getAllExemptions(@PathVariable("id") Long id) {
        return exemptionDao.getExemptionById(id);
    }

    @GetMapping("user/{id}")
    public UserExemption getUserData(@PathVariable("id") Long id) {
        return UserExemption.merge(exemptionDao.getUserExemptionsById(id));
    }

    @GetMapping(value = "document/{id}", produces = {MediaType.APPLICATION_PDF_VALUE})
    public byte[] getDocumentById(@PathVariable("id") Long id) {
        return exemptionDao.getDocumentById(id).data();
    }
}

