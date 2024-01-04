package com.jxx.vacation.api.annualleave.presentation;

import com.jxx.vacation.core.vacation.infra.AnnualLeaveHistoryRepository;
import com.jxx.vacation.core.vacation.infra.AnnualLeaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AnnualLeaveController {

    private final AnnualLeaveRepository annualLeaveRepository;
    private final AnnualLeaveHistoryRepository annualLeaveHistoryRepository;

    @Transactional
    @PostMapping("/annual-leaves")
    public void save() {

    }
}
