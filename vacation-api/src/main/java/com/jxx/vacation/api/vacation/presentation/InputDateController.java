package com.jxx.vacation.api.vacation.presentation;

import com.jxx.vacation.core.vacation.domain.entity.Leave;
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
        Organization organization = new Organization(
                "JXX", "제이주식회사",
                "J01001", "마케팅팀",
                "J01000", "경영지원본부");
        organizationRepository.save(organization);

        MemberLeave memberLeave1 = MemberLeave.builder()
                .name("이재헌")
                .memberId("U00001")
                .leave(new Leave(15F, 15F))
                .enteredDate(LocalDate.of(2023, 8, 5))
                .experienceYears(1)
                .organization(organization)
                .build();
        memberLeaveRepository.save(memberLeave1);

        MemberLeave memberLeave2 = MemberLeave.builder()
                .name("하니")
                .memberId("U00002")
                .leave(new Leave(15F, 15F))
                .enteredDate(LocalDate.of(2023, 1, 5))
                .experienceYears(1)
                .organization(organization)
                .build();
        memberLeaveRepository.save(memberLeave2);
        log.info("저장 완료");
    }
}
