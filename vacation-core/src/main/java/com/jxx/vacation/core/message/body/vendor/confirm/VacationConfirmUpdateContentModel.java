package com.jxx.vacation.core.message.body.vendor.confirm;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class VacationConfirmUpdateContentModel {
    private String delegatorId;
    private String delegatorName;
    private String reason;
    private String departmentId;
    private List<VacationDurationModel> vacationDurations;

    public VacationConfirmUpdateContentModel(String delegatorId, String delegatorName, String reason, String departmentId, List<VacationDurationModel> vacationDurations) {
        this.delegatorId = delegatorId;
        this.delegatorName = delegatorName;
        this.reason = reason;
        this.departmentId = departmentId;
        this.vacationDurations = vacationDurations;
    }

    // 우선권을 가지네
    public static VacationConfirmUpdateContentModel from(Map<String, Object> messageBody) {
        String delegatorId = String.valueOf(messageBody.get("delegator_id"));
        String delegatorName = String.valueOf(messageBody.get("delegator_name"));
        String reason = (String) messageBody.get("reason");
        String departmentId = (String) messageBody.get("department_id");
        List<VacationDurationModel> vacationDurations = (List<VacationDurationModel>) messageBody.get("vacation_durations");
        return new VacationConfirmUpdateContentModel(delegatorId, delegatorName, reason, departmentId, vacationDurations);
    }
}
