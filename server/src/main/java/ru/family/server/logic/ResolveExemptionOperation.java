package ru.family.server.logic;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.family.server.model.Exemption;
import ru.family.server.model.Worksheet;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ResolveExemptionOperation {

    public List<Exemption> process(Worksheet worksheet) {
        return List.of();
    }
}
