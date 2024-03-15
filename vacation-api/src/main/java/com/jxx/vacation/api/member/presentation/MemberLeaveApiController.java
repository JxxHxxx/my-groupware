package com.jxx.vacation.api.member.presentation;

import com.jxx.vacation.api.member.application.MemberLeaveService;
import com.jxx.vacation.api.member.dto.response.MemberLeaveResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberLeaveApiController {

    private final MemberLeaveService memberLeaveService;

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

    @GetMapping("/api/departments/{department-id}/member-leaves")
    public ResponseEntity<?> getDepartmentMembers(@PathVariable("department-id") String departmentId, @RequestParam("cid") String companyId) {
        List<MemberLeaveResponse> responses = memberLeaveService.findSameDepartmentMembers(companyId, departmentId);
        return ResponseEntity.ok(responses);
    }
}
