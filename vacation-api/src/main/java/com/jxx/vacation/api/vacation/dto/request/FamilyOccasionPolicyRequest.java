package com.jxx.vacation.api.vacation.dto.request;

import java.util.List;

public record FamilyOccasionPolicyRequest(
        String adminId,
        List<FamilyOccasionPolicyForm> form
) {
}
