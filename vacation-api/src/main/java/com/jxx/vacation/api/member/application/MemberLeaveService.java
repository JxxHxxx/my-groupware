package com.jxx.vacation.api.member.application;

import com.jxx.vacation.api.member.dto.response.MemberLeaveResponse;
import com.jxx.vacation.core.vacation.domain.entity.MemberLeave;
import com.jxx.vacation.core.vacation.domain.entity.Organization;
import com.jxx.vacation.core.vacation.infra.MemberLeaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberLeaveService {

    private final MemberLeaveRepository memberLeaveRepository;

    public MemberLeaveResponse findMemberLeave(String memberId) {
        MemberLeave memberLeave = memberLeaveRepository.findByMemberId(memberId).
        orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));
        Organization organization = memberLeave.getOrganization();
        return new MemberLeaveResponse(
                memberLeave.getPk(),
                memberLeave.getMemberId(),
                memberLeave.getName(),
                memberLeave.getExperienceYears(),
                memberLeave.getEnteredDate(),
                memberLeave.receiveTotalLeave(),
                memberLeave.receiveRemainingLeave(),
                organization.getCompanyId(),
                organization.getCompanyName(),
                organization.getDepartmentId(),
                organization.getDepartmentName());
    }
}
