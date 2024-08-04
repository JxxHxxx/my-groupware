package com.jxx.groupware.core.vacation.domain.service;

import com.jxx.groupware.core.vacation.domain.dto.RequestVacationDuration;
import com.jxx.groupware.core.vacation.domain.entity.CompanyVacationTypePolicy;
import com.jxx.groupware.core.vacation.domain.entity.VacationType;
import com.jxx.groupware.core.vacation.domain.exeception.VacationClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** 싱글턴으로 사용하면 안됩니다. **/

@Slf4j
@RequiredArgsConstructor
public class VacationTypePolicyValidator {
    /**
     * 휴가일수 조정 값
     * 예를 들어 7일 짜리 경조 휴가가 있다고 할 때, 8월 3일이 휴가 시작일일 경우, 8월 9일에 종료되어야 한다.
     * 8월 3일 + 7 = 8월 10일이 아닌 8월 9일이 되어야 하므로 조정 값을 둔다.
     **/
    private static final Long ADJUST_VACATION_DAY_VALUE = 1l;

    /**
     * 하나의 고객사의 CompanyVacationTypePolicy 를 주입한다.
     **/
    private final List<CompanyVacationTypePolicy> companyPolicies;

    /**
     * @param vacationType  : 클라이언트로부터 받은 vacationType
     * @param companyId :  클라이언트로부터 받은 companyId
     * @param requestVacationDuration : 클라이언트로부터 받은 requestVacationDuration
     */

    public void validate(VacationType vacationType, String companyId, RequestVacationDuration requestVacationDuration) {
        Optional<CompanyVacationTypePolicy> oCompanyVacationTypePolicy = companyPolicies.stream()
                .filter(cp -> Objects.equals(vacationType, cp.getVacationType()))
                .filter(cp -> Objects.equals(companyId, cp.getCompanyId())) // 보완 로직
                .findFirst();
        CompanyVacationTypePolicy companyVacationTypePolicy = oCompanyVacationTypePolicy
                .orElseThrow(() -> new VacationClientException("올바르지 않은 접근입니다."));

        LocalDateTime startDateTime = requestVacationDuration.getStartDateTime();

        Float vacationDay = companyVacationTypePolicy.getVacationDay();
        if (vacationDay != vacationDay.longValue()) {
            log.warn("vacationDay:{} 은 소수점이 존재합니다. Long Type 으로 변환하는 과정에서 소수점은 삭제됩니다.", vacationDay);
        }
        Long longVacationDay = companyVacationTypePolicy.getVacationDay() // 만약 Float VacationDay 가 소수점이 존재한다면 내림이 되니 주의
                .longValue();
        // 백엔드에서 검증한 휴가 종료일
        LocalDateTime expectedEndDateTime = LocalDateTime.of(
                startDateTime.plusDays(longVacationDay - ADJUST_VACATION_DAY_VALUE).toLocalDate(),
                LocalTime.of(23, 59));
        // 클라이언트 측으로부터 받은 휴가 종료일
        LocalDateTime requestEndDateTime = requestVacationDuration.getEndDateTime();

        // 백엔드에서 검증한 휴가 종료일과 클라리언트 측으로부터 받은 휴가 종료일이 일치하지 않을 때 로깅 및 예외
        if (!Objects.equals(expectedEndDateTime, requestEndDateTime)) {
            log.warn("백엔드에서 검증한 종료일:{} 클라이언트에서 보낸 종료일:{}", expectedEndDateTime, requestEndDateTime);
            throw new VacationClientException("휴가 종료일이 올바르게 설정되지 않았습니다. " +
                    "\n예상되는 휴가 종료일:" + expectedEndDateTime +
                    "\n클라이언트로부터 받은 휴가 종료일:" + requestEndDateTime);
        }
    }
}
