package com.jxx.vacation.api.member.presentation;

import com.jxx.vacation.api.member.application.AuthService;
import com.jxx.vacation.api.member.application.MemberLeaveService;
import com.jxx.vacation.api.member.dto.request.MemberSearchCondition;
import com.jxx.vacation.api.member.dto.response.MemberLeaveResponse;
import com.jxx.vacation.api.member.dto.response.MemberProjection;
import com.jxx.vacation.api.vacation.dto.response.ResponseResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberLeaveApiController {

    private final MemberLeaveService memberLeaveService;
    private final AuthService authService;

    @GetMapping("/api/member-leaves/{member-id}")
    public ResponseEntity<?> getMemberLeave(@PathVariable("member-id") String memberId) {
        MemberLeaveResponse response = memberLeaveService.findMemberLeave(memberId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/companies/{company-id}/member-leaves")
    public ResponseEntity<?> getCompanyMemberLeave(@PathVariable("company-id") String companyId, @RequestParam List<String> membersId) {
        List<MemberLeaveResponse> responses = memberLeaveService.findCompanyMembers(companyId, membersId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/api/companies/{company-id}/members")
    public ResponseEntity<?> searchCompanyMember(@PathVariable("company-id") String companyId,
                                                 @ModelAttribute MemberSearchCondition searchCondition) {

        List<MemberProjection> responses = memberLeaveService.search(searchCondition, companyId);
        return ResponseEntity.ok(new ResponseResult(200, "사용자 검색", responses));
    }

    @GetMapping("/api/departments/{department-id}/member-leaves")
    public ResponseEntity<?> getDepartmentMembers(@PathVariable("department-id") String departmentId, @RequestParam("cid") String companyId) {
        List<MemberLeaveResponse> responses = memberLeaveService.findSameDepartmentMembers(companyId, departmentId);
        return ResponseEntity.ok(responses);
    }
}
