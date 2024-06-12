package com.jxx.vacation.core.message.body.vendor.confirm;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class VacationUpdateMessageForm {

    private final Long vacationId;
    private final DocumentType documentType;
    private final String companyId;
    private final String delegatorId;
    private final String delegatorName;
    private final String reason;
    private final List<VacationDurationModel> vacationDurations;
    private final String departmentId;

}
