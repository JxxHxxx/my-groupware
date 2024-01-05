package com.jxx.vacation.api.vacation.presentation;

import com.jxx.vacation.core.vacation.infra.VacationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class VacationController {

    private final VacationRepository vacationRepository;
    @Transactional
    @PostMapping("/vacations")
    public void save() {

    }
}
