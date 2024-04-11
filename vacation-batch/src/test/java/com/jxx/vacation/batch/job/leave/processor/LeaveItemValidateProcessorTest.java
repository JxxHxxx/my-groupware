package com.jxx.vacation.batch.job.leave.processor;

import com.jxx.vacation.batch.job.leave.item.LeaveItem;
import com.jxx.vacation.core.vacation.domain.entity.LeaveDeduct;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.jxx.vacation.core.vacation.domain.entity.VacationType.MORE_DAY;
import static org.assertj.core.api.Assertions.*;

class LeaveItemValidateProcessorTest {

    private final LeaveItemValidateProcessor leaveItemValidateProcessor = new LeaveItemValidateProcessor();

    @DisplayName("vacationStatus=ONGOING 이 아닐 때 " +
            "LeaveItemValidateProcessor 를 실행할 경우 " +
            "item 은 반환되지 않는다.(null 이다.)")
    @ParameterizedTest
    @ValueSource(strings = {"CREATE", "REJECT", "REQUEST","APPROVED" ,"CANCELED", "COMPLETED", "FAIL" ,"ERROR"})
    void leave_item_validate_fail_cause_not_ongoing_vacation_status(String vacationStatus) throws Exception {
        //given
        LocalDateTime vacationStartDateTime = LocalDateTime.of(2025, 12, 12, 0, 0,0);
        LocalDateTime vacationEndDateTime = LocalDateTime.of(2025, 12, 13, 0,0,0);
        LeaveItem leaveItem = new LeaveItem(
                "U00013",
                LocalDateTime.now(),
                true,
                15f,
                15f,
                "이재헌",
                "U00013",
                2,
                LocalDate.of(2023, 12, 12),
                100l,
                LeaveDeduct.DEDUCT.name(),
                vacationStatus,
                MORE_DAY.name(),
                vacationStartDateTime,
                vacationEndDateTime,
                "JXX",
                "departId",
                true);
        //when
        LeaveItem processedItem = leaveItemValidateProcessor.process(leaveItem);
        //then
        assertThat(processedItem).isNull();
    }

    @DisplayName("vacationStatus = ONGOING 일 때 " +
            "LeaveItemValidateProcessor 를 실행할 경우" +
            "vacationStatus = COMPLETED, DeductedAmount 는 휴가 일 수 만큼으로 변경된다.")
    @Test
    void leave_item_validate_success() throws Exception {
        //given
        String vacationStatus = "ONGOING";
        LocalDateTime vacationStartDateTime = LocalDateTime.of(2025, 12, 12, 0, 0,0);
        LocalDateTime vacationEndDateTime = LocalDateTime.of(2025, 12, 13, 0,0,0);
        LeaveItem leaveItem = new LeaveItem(
                "U00013",
                LocalDateTime.now(),
                true,
                15f,
                15f,
                "이재헌",
                "U00013",
                2,
                LocalDate.of(2023, 12, 12),
                100l,
                LeaveDeduct.DEDUCT.name(),
                vacationStatus,
                MORE_DAY.name(),
                vacationStartDateTime,
                vacationEndDateTime,
                "JXX",
                "departId",
                true);
        //when
        LeaveItem processedItem = leaveItemValidateProcessor.process(leaveItem);
        //then
        assertThat(processedItem.getVacationStatus()).isEqualTo("COMPLETED");
        assertThat(processedItem.getDeductedAmount()).isEqualTo(2f);
    }
}