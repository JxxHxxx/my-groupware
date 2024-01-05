package com.jxx.vacation.api.vacation.presentation;

import com.jxx.vacation.core.vacation.domain.entity.MemberLeave;
import com.jxx.vacation.core.vacation.domain.entity.Organization;
import com.jxx.vacation.core.vacation.infra.MemberLeaveRepository;
import com.jxx.vacation.core.vacation.infra.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Slf4j
@RestController
@RequiredArgsConstructor
public class InputDateController {

    private final MemberLeaveRepository memberLeaveRepository;
    private final OrganizationRepository organizationRepository;


    @Transactional
    @PostMapping("/members")
    public void save() {

        Organization organization = new Organization("JXX", "제이주식회사", "J00001", "마케팅팀");
        organizationRepository.save(organization);

        MemberLeave memberLeave = MemberLeave.builder()
                .name("이재헌")
                .memberId("U00001")
                .remainingLeave(15F)
                .enteredDate(LocalDate.of(2023, 8, 5))
                .experienceYears(1)
                .organization(organization)
                .build();
        memberLeaveRepository.save(memberLeave);
        log.info("저장 완료");


    }
}
