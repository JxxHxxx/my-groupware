package com.jxx.groupware.api.vacation.dto.request;

import com.jxx.groupware.api.member.application.UserSession;
import jakarta.validation.constraints.NotNull;


public record CommonVacationServiceForm(
        @NotNull
        UserSession userSession,
        @NotNull
        CommonVacationForm commonVacationForm
) {

}
