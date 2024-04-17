package com.jxx.vacation.api.vacation.application;


import com.jxx.vacation.api.member.application.UserSession;
import com.jxx.vacation.api.vacation.dto.request.CommonVacationForm;
import com.jxx.vacation.api.vacation.dto.request.CommonVacationServiceForm;
import com.jxx.vacation.api.vacation.dto.response.CommonVacationServiceResponse;
import com.jxx.vacation.api.vacation.dto.response.VacationServiceResponse;
import com.jxx.vacation.core.vacation.domain.entity.Vacation;
import com.jxx.vacation.core.vacation.domain.entity.VacationDuration;
import com.jxx.vacation.core.vacation.domain.exeception.VacationClientException;
import com.jxx.vacation.core.vacation.infra.VacationDurationRepository;
import com.jxx.vacation.core.vacation.infra.VacationRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class VacationAdminServiceTest {
    @Autowired
    VacationAdminService vacationAdminService;
    @Autowired
    VacationRepository vacationRepository;
    @Autowired
    VacationDurationRepository vacationDurationRepository;

    @DisplayName("테스트")
    @Test
    void test_success_case() {
        //given
        UserSession userSession = new UserSession("JXX", "제이주식회사", "U00001", "이재헌", "J00001", "IT센터");
        LocalDate vacationDate = LocalDate.of(2024, 4, 17);
        CommonVacationForm form = new CommonVacationForm("JXX", false, false, List.of(vacationDate));
        CommonVacationServiceForm serviceForm = new CommonVacationServiceForm(userSession, form);

        //when
        CommonVacationServiceResponse response = vacationAdminService.assignCommonVacation(serviceForm);

        VacationServiceResponse vacationServiceResponse = response.vacations().get(0);
        assertThat(vacationServiceResponse.vacationDuration())
                .extracting("startDateTime").containsExactly(vacationDate.atStartOfDay());

        assertThat(vacationServiceResponse.vacationDuration())
                .extracting("endDateTime").containsExactly(vacationDate.atTime(23, 59, 59));
        //then
    }

    @DisplayName("동일한 요일을 공동 연차로 중복 신청할 시, VacationClientException 발생한다.")
    @Test
    void assign_common_vacation_fail_cause_duplication_vacation_date() {
        //given
        UserSession userSession = new UserSession("JXX", "제이주식회사", "U00001", "이재헌", "J00001", "IT센터");
        LocalDate vacationDate = LocalDate.of(2024, 4, 17);
        CommonVacationForm form = new CommonVacationForm("JXX", false, false, List.of(vacationDate));
        CommonVacationServiceForm serviceForm = new CommonVacationServiceForm(userSession, form);
        //when
        CommonVacationServiceResponse firstResponse = vacationAdminService.assignCommonVacation(serviceForm);

        Assertions.assertThatThrownBy(() -> vacationAdminService.assignCommonVacation(serviceForm))
                .isInstanceOf(VacationClientException.class)
                .hasMessageContaining("은 이미 공동 연차로 등록되어 있는 날짜입니다.");
        //when

        //then
    }
}