package com.jxx.groupware.api.organization.presentation;


import com.jxx.groupware.api.organization.application.OrganizationService;
import com.jxx.groupware.api.organization.application.OrganizationTreeResponse;
import com.jxx.groupware.api.organization.dto.request.CompanyCodeCreateForm;
import com.jxx.groupware.api.organization.dto.request.OrganizationSearchCond;
import com.jxx.groupware.api.organization.dto.response.CompanyCodeResponse;
import com.jxx.groupware.api.organization.dto.response.CompanyDepartmentResponse;
import com.jxx.groupware.api.organization.dto.response.OrganizationServiceResponse;
import com.jxx.groupware.api.vacation.dto.response.ResponseResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrganizationApiController {
    private final OrganizationService organizationService;

    @GetMapping("/api/organizations")
    public ResponseEntity<?> searchOrganization(@ModelAttribute @Valid OrganizationSearchCond searchCond) {
        List<OrganizationServiceResponse> responses =  organizationService.searchOrganization(searchCond);
        return ResponseEntity.ok(new ResponseResult<>(200, "부서 검색 완료", responses));
    }

    @GetMapping("/api/organizations/companies/{company-id}/tree")
    public ResponseEntity<?> searchOrganizationTree(@PathVariable("company-id") String companyId) {
        List<OrganizationTreeResponse> responses = organizationService.makeTree(companyId);

        return ResponseEntity.ok(new ResponseResult<>(200, "회사 조직도 조회", responses));
    }

    /** /admin 으로 시작하면 필터 타야되서... 임시로 test 로 시작 **/
    @PostMapping("/test/company-codes")
    public ResponseEntity<?> createCompanyCode(@RequestBody CompanyCodeCreateForm form) {
        CompanyCodeResponse response = organizationService.createCompanyCode(form);
        return ResponseEntity.status(200)
                .body(new ResponseResult<>(201, "회사 코드 등록", response));
    }

    @GetMapping("/test/company-codes")
    public ResponseEntity<?> findCompanyCode() {
        List<CompanyCodeResponse> responses  = organizationService.findCompanyCode();
        return ResponseEntity.ok(new ResponseResult<>(200, "사용중인 회사 코드 검색", responses));
    }

    @GetMapping("/test/company-codes/{company-code}")
    public ResponseEntity<?> findCompanyDepartments(@PathVariable("company-code") String companyCode) {
        List<CompanyDepartmentResponse> responses = organizationService.findCompanyDepartments(companyCode);
        return ResponseEntity.ok(new ResponseResult<>(200, "활성화된 부서 검색", responses));
    }
}
