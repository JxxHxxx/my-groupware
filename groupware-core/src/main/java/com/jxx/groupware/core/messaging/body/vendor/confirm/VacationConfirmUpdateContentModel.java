package com.jxx.groupware.core.messaging.body.vendor.confirm;

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
    private Long contentPk;
    private List<VacationDurationModel> vacationDurations;

    public VacationConfirmUpdateContentModel(String delegatorId, String delegatorName, String reason, Long contentPk, List<VacationDurationModel> vacationDurations) {
        this.delegatorId = delegatorId;
        this.delegatorName = delegatorName;
        this.reason = reason;
        this.contentPk = contentPk;
        this.vacationDurations = vacationDurations;
    }

    // 우선권을 가지네
    public static VacationConfirmUpdateContentModel from(Map<String, Object> messageBody) {
        String delegatorId = String.valueOf(messageBody.get("delegator_id"));
        String delegatorName = String.valueOf(messageBody.get("delegator_name"));
        String reason = (String) messageBody.get("reason");
        // Long 타입을 변환하지 Integer 타입으로 인식하고 있어 캐스팅으로 처리
        int tempContentPk = Integer.parseInt(String.valueOf(messageBody.get("content_pk")));
        Long contentPk = Long.parseLong(String.valueOf(tempContentPk));

        List<VacationDurationModel> vacationDurations = (List<VacationDurationModel>) messageBody.get("vacation_durations");
        return new VacationConfirmUpdateContentModel(delegatorId, delegatorName, reason, contentPk, vacationDurations);
    }
}
