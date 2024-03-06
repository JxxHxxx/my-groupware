package com.jxx.vacation.api.vacation.dto.request;

public record FamilyOccasionPolicyForm (
        String companyId,
        String vacationType,
        Float vacationDay
) {
}
