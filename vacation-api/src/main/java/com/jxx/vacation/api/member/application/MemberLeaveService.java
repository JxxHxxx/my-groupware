package com.jxx.vacation.api.member.application;

import com.jxx.vacation.core.common.pagination.PageService;
import com.jxx.vacation.api.member.dto.request.MemberLeaveSearchParam;
import com.jxx.vacation.api.member.dto.request.MemberSearchCondition;
import com.jxx.vacation.api.member.dto.response.MemberLeaveResponse;
import com.jxx.vacation.api.member.dto.response.MemberProjection;
import com.jxx.vacation.api.member.query.MemberDynamicMapper;
import com.jxx.vacation.core.vacation.domain.entity.MemberLeave;
import com.jxx.vacation.core.vacation.domain.entity.Organization;
import com.jxx.vacation.core.vacation.domain.exeception.MemberLeaveException;
import com.jxx.vacation.core.vacation.infra.MemberLeaveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberLeaveService {
    private final MemberLeaveRepository memberLeaveRepository;
    private final MemberDynamicMapper memberDynamicMapper;

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

    public PageImpl<MemberLeaveResponse> findSameDepartmentMembers(MemberLeaveSearchParam searchParam, int page, int size) {
        List<MemberLeave> departmentMembers = memberLeaveRepository.findDepartmentMembers(
                searchParam.getCompanyId(), searchParam.getDepartmentId());
        List<MemberLeaveResponse> memberLeaveResponses = entityToPojo(departmentMembers);
        PageService pageService = new PageService(page, size);

       return pageService.convertToPage(memberLeaveResponses);

    }

    private static List<MemberLeaveResponse> entityToPojo(List<MemberLeave> departmentMembers) {
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

    public List<MemberLeaveResponse> findCompanyMembers(String companyId, List<String> membersId) {
        // memberId 는 Unique 컬럼이기 때문에 사이즈만 동일해도 검증 가능
        List<MemberLeave> companyMembers = memberLeaveRepository.findCompanyMembers(companyId, membersId);
        boolean containsOtherCompanyMember = companyMembers.size() != membersId.size();
        if (containsOtherCompanyMember) {
            try {
                throw new MemberLeaveException(companyId + "에 소속된 사용자가 아닙니다.");
            } catch (MemberLeaveException e) {
                throw new RuntimeException(e);
            }
        }

        return entityToPojo(companyMembers);
    }
    public List<MemberProjection> search(MemberSearchCondition searchCondition, String companyId) {
        if (!Objects.equals(searchCondition.getCompanyId(), companyId)) {
            log.warn("권장되지 않는 API 호출 입니다. queryParam:{} path-variable:{}", searchCondition.getCompanyId(), companyId);
            searchCondition.changeCompanyId(companyId);
        }
        searchCondition.changeOnlyActive();
        return memberDynamicMapper.search(searchCondition);
    }
}
