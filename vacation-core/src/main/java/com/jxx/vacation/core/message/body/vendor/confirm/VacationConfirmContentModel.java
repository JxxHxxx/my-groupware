package com.jxx.vacation.core.message.body.vendor.confirm;


import lombok.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class VacationConfirmContentModel {
    private String title;
    private String delegatorId;
    private String reason;
    private String requesterId;
    private String requesterName;
    private String departmentId;
    private String departmentName;
    private List<VacationDurationModel> vacationDurations;

    public static VacationConfirmContentModel from(Map<String, Object> messageBody) {
        String title = (String) messageBody.get("title");
        String delegatorId = String.valueOf(messageBody.get("delegator_id"));
        String reason = (String) messageBody.get("reason");
        String requesterId = (String) messageBody.get("requester_id");
        String requesterName = (String) messageBody.get("requester_name");
        String departmentId = (String) messageBody.get("department_id");
        String departmentName = (String) messageBody.get("department_name");
        List<VacationDurationModel> vacationDurations = (List<VacationDurationModel>) messageBody.get("vacation_durations");
        return new VacationConfirmContentModel(title, delegatorId, reason, requesterId, requesterName, departmentId, departmentName, vacationDurations);
    }
    public Map<String, Object> toMap() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("title", title);
        parameters.put("delegator_id", delegatorId);
        parameters.put("reason", reason);
        parameters.put("requester_id", requesterId);
        parameters.put("requester_name", requesterName);
        parameters.put("department_id", departmentId);
        parameters.put("department_name", departmentName);
        parameters.put("vacation_durations", vacationDurations);

        return parameters;
    }
}
