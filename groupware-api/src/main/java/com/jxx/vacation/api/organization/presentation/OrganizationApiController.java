package com.jxx.vacation.api.organization.presentation;


import com.jxx.vacation.api.organization.application.OrganizationService;
import com.jxx.vacation.api.organization.application.OrganizationTreeResponse;
import com.jxx.vacation.api.vacation.dto.response.ResponseResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrganizationApiController {

    private final OrganizationService organizationService;

    @GetMapping("/api/organizations/companies/{company-id}")
    public ResponseEntity<?> searchOrganization(@PathVariable("company-id") String companyId) {
        List<OrganizationTreeResponse> responses = organizationService.makeTree(companyId);

        return ResponseEntity.ok(new ResponseResult<>(200, "회사 조직도 조회", responses));
    }


}
