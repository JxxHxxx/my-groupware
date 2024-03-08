package com.jxx.vacation.api.member.application;

import com.jxx.vacation.api.member.dto.response.MemberLeaveResponse;
import com.jxx.vacation.core.vacation.domain.entity.MemberLeave;
import com.jxx.vacation.core.vacation.domain.entity.Organization;
import com.jxx.vacation.core.vacation.infra.MemberLeaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<MemberLeaveResponse> findSameDepartmentMembers(String companyId, String departmentId) {
        List<MemberLeave> departmentMembers = memberLeaveRepository.findDepartmentMembers(companyId, departmentId);

        return departmentMembers.stream()
                .map(memberLeave -> new MemberLeaveResponse(
                        memberLeave.getPk(),
                        memberLeave.getMemberId(),
                        memberLeave.getName(),
                        memberLeave.getExperienceYears(),
                        memberLeave.getEnteredDate(),
                        memberLeave.receiveTotalLeave(),
                        memberLeave.receiveRemainingLeave(),
                        memberLeave.getOrganization().getCompanyId(),
                        memberLeave.getOrganization().getCompanyName(),
                        memberLeave.getOrganization().getDepartmentId(),
                        memberLeave.getOrganization().getDepartmentName()))
                .toList();
    }
}
