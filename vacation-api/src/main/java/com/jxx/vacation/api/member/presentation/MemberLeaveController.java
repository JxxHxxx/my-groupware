package com.jxx.vacation.api.member.presentation;

import com.jxx.vacation.api.member.application.MemberLeaveService;
import com.jxx.vacation.api.member.dto.response.MemberLeaveResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberLeaveController {

    private final MemberLeaveService memberLeaveService;

    @GetMapping("/api/member-leaves/{member-id}")
    public ResponseEntity<?> getMemberLeave(@PathVariable("member-id") String memberId) {
        MemberLeaveResponse response = memberLeaveService.findMemberLeave(memberId);
        return ResponseEntity.ok(response);

    }
}
