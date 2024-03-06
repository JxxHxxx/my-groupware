package com.jxx.vacation.api.vacation.application;

import com.jxx.vacation.api.vacation.dto.request.FamilyOccasionPolicyForm;
import com.jxx.vacation.api.vacation.dto.response.FamilyOccasionPolicyResponse;
import com.jxx.vacation.core.vacation.domain.entity.FamilyOccasionPolicy;
import com.jxx.vacation.core.vacation.domain.entity.VacationType;
import com.jxx.vacation.core.vacation.infra.FamilyOccasionPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class VacationAdminService {

    private final FamilyOccasionPolicyRepository familyOccasionPolicyRepository;

    @Transactional
    public List<FamilyOccasionPolicyResponse> addFamilyOccasionPolicies(List<FamilyOccasionPolicyForm> forms) {
        List<FamilyOccasionPolicy> policies = forms.stream()
                .map(form -> new FamilyOccasionPolicy(
                        form.companyId(),
                        VacationType.valueOf(form.vacationType()),
                        form.vacationDay()))
                .toList();

        List<FamilyOccasionPolicy> savedPolicies = familyOccasionPolicyRepository.saveAll(policies);
        return savedPolicies.stream()
                .map(policy -> new FamilyOccasionPolicyResponse(policy.getCompanyId(), policy.getVacationType(), policy.getVacationDay()))
                .toList();

    }
}
