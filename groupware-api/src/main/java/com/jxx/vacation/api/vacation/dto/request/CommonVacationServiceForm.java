package com.jxx.vacation.api.vacation.dto.request;

import com.jxx.vacation.api.member.application.UserSession;
import jakarta.validation.constraints.NotNull;


public record CommonVacationServiceForm(
        @NotNull
        UserSession userSession,
        @NotNull
        CommonVacationForm commonVacationForm
) {

}
