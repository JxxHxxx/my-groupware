package com.jxx.vacation.core.vacation.domain.entity;


import com.jxx.vacation.core.vacation.domain.exeception.VacationClientException;
import com.jxx.vacation.testUtil.CoreEntityFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Slf4j
class VacationManagerTest {

    // 인스턴스 생성 시, 가지는 특징
    @DisplayName("createVacation 정적 팩터리 메서드 사용 시, 인스턴스의 필드는 아래와 같은 특징을 가진다. " +
            "Vacation:신청자의 ID 값을 가진다.  " +
            "VacationDate:휴가 일수를 나타낸다. " +
            "MemberLeave:메서드 인자로 넣은 MemberLeave가 적용된다.")
    @Test
    void instantiate() {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(2l);
        VacationDuration vacationDuration = new VacationDuration(VacationType.MORE_DAY, startDate, endDate);

        Organization organization = CoreEntityFactory.defalutOrganization();
        MemberLeave memberLeave = CoreEntityFactory.defaultMemberLeave(organization); // memberId == requesterId

        //when
        VacationManager vacationManager = VacationManager.createVacation(vacationDuration, memberLeave);

        assertThat(vacationManager.getVacation()).extracting("requesterId").isEqualTo(memberLeave.getMemberId());
        assertThat(vacationManager.receiveVacationDate()).isEqualTo(3f);
        assertThat(vacationManager.getMemberLeave()).isEqualTo(memberLeave);
    }

    @DisplayName("Vacation Status 가 REQUEST, APPROVED, ONGOING 에 포함되는 휴가 날짜를 " +
            "다시 휴가로 신청할 수 없다." +
            "만약 해당 상태를 가지는 휴가 날짜를 휴가로 요청할 경우 VacationClientException 이 발생한다.")
    @EnumSource(names = {"REQUEST", "APPROVED", "ONGOING"})
    @ParameterizedTest
    void validate_vacation_dates_are_duplication_throw_exception_case(VacationStatus vacationStatus) {
        //given - 휴가 신청
        LocalDateTime startDate = LocalDateTime.of(2024, 3, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 3, 4, 0, 0);
        VacationDuration vacationDuration = new VacationDuration(VacationType.MORE_DAY, startDate, endDate);

        Organization organization = CoreEntityFactory.defalutOrganization();
        MemberLeave memberLeave = CoreEntityFactory.defaultMemberLeave(organization); // memberId == requesterId
        VacationManager vacationManager = VacationManager.createVacation(vacationDuration, memberLeave);

        // 기존에 존재하고 있는 아직 사용 전인 휴가
        LocalDateTime startDate2 = LocalDateTime.of(2024, 3, 2, 0, 0);
        LocalDateTime endDate2 = LocalDateTime.of(2024, 3, 3, 0, 0);
        VacationDuration vacationDuration2 = new VacationDuration(VacationType.MORE_DAY, startDate2, endDate2);

        Vacation existedVacation = new Vacation("T0001", "TJX", LeaveDeduct.DEDUCT, vacationDuration2, true, vacationStatus);

        //WHEN - THEN
        assertThatThrownBy(() -> vacationManager.validateVacationDatesAreDuplicated(List.of(existedVacation)))
                .isInstanceOf(VacationClientException.class);
    }

    @DisplayName("Vacation Status 가 CREATE, REJECT, CANCELED, COMPLETED, FAIL, ERROR 에 포함되는 날짜는" +
            "다시 휴가로 신청할 수 있다. 즉,VacationClientException 이 발생하지 않는다." +
            "엄밀히 말해, COMPLETED 의 경우에는 이미 종료된 휴가를 의미하기 때문에 생성 시점 이전 일이다. ")
    @EnumSource(names = {"CREATE", "REJECT", "CANCELED", "COMPLETED", "FAIL", "ERROR"})
    @ParameterizedTest
    void validate_vacation_dates_are_duplication_not_throw_exception_case(VacationStatus vacationStatus) {
        LocalDateTime startDate = LocalDateTime.of(2024, 3, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 3, 4, 0, 0);
        VacationDuration vacationDuration = new VacationDuration(VacationType.MORE_DAY, startDate, endDate);

        Organization organization = CoreEntityFactory.defalutOrganization();
        MemberLeave memberLeave = CoreEntityFactory.defaultMemberLeave(organization); // memberId == requesterId
        VacationManager vacationManager = VacationManager.createVacation(vacationDuration, memberLeave);

        // 휴가 상태가 REQUEST, APPROVED, ONGOING 아닌 경우
        LocalDateTime startDate2 = LocalDateTime.of(2024, 3, 2, 0, 0);
        LocalDateTime endDate2 = LocalDateTime.of(2024, 3, 3, 0, 0);
        VacationDuration vacationDuration2 = new VacationDuration(VacationType.MORE_DAY, startDate2, endDate2);

        //WHEN - THEN
        Vacation existedVacation = new Vacation("T0001", "TJX", LeaveDeduct.DEDUCT, vacationDuration2, true, vacationStatus);

        assertThatCode(() -> vacationManager.validateVacationDatesAreDuplicated(List.of(existedVacation)))
                .doesNotThrowAnyException();
    }

    @DisplayName("잔여 연차일 - (현재 결재 진행 중인 휴가 + 신청한 휴가일 수) 가 0 보다 작은 음수일 경우 " +
            "검증에 실패하고 VacationClientException 예외가 발생한다.")
    @Test
    void validate_remaining_leave_is_bigger_than_confirming_vacations_and_throw_exception_case() {
        // CASE 잔여 연차일 15일, 현재 결재 진행 중인 휴가일 12(4, 4, 4)일, 신청한 휴가일 4일
        // 15 - (12 + 4) < 0 이므로 예외 발생

        //GIVEN
        LocalDateTime startDate = LocalDateTime.of(2024, 3, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 3, 4, 0, 0);
        VacationDuration vacationDuration = new VacationDuration(VacationType.MORE_DAY, startDate, endDate);

        Organization organization = CoreEntityFactory.defalutOrganization();
        MemberLeave memberLeave = CoreEntityFactory.defaultMemberLeave(organization); // memberId == requesterId
        VacationManager vacationManager = VacationManager.createVacation(vacationDuration, memberLeave);

        Vacation vacation1 = Vacation.builder()
                .vacationStatus(VacationStatus.APPROVED)
                .vacationDuration(
                        new VacationDuration(
                                VacationType.MORE_DAY,
                                LocalDateTime.of(2024, 4, 1, 0, 0),
                                LocalDateTime.of(2024, 4, 4, 0, 0)))
                .requesterId("T0001")
                .deducted(true)
                .build();

        Vacation vacation2 = Vacation.builder()
                .vacationStatus(VacationStatus.REQUEST)
                .vacationDuration(new VacationDuration(
                                VacationType.MORE_DAY,
                                LocalDateTime.of(2024, 5, 1, 0, 0),
                                LocalDateTime.of(2024, 5, 4, 0, 0)))
                .requesterId("T0001")
                .deducted(true)
                .build();

        Vacation vacation3 = Vacation.builder()
                .vacationStatus(VacationStatus.REQUEST)
                .vacationDuration(new VacationDuration(
                                VacationType.MORE_DAY,
                                LocalDateTime.of(2024, 6, 1, 0, 0),
                                LocalDateTime.of(2024, 6, 4, 0, 0)))
                .requesterId("T0001")
                .deducted(true)
                .build();

        List<Vacation> vacations = List.of(vacation1, vacation2, vacation3);

        //WHEN - THEN
        assertThatThrownBy(() -> vacationManager.validateRemainingLeaveIsBiggerThanConfirmingVacationsAnd(vacations))
                .isInstanceOf(VacationClientException.class);
    }

    @DisplayName("잔여 연차일 - (현재 결재 진행 중인 휴가 + 신청한 휴가일 수) 가 0을 포함한 양의 정수일 경우 " +
            "검증에 통과하고 예외가 발생하지 않는다. ")
    @ValueSource(ints = {1, 3})
    @ParameterizedTest()
    void validate_remaining_leave_is_bigger_than_confirming_vacations_and_not_throw_exception_case(int endDayOfMonth) {
        // CASE 잔여 연차일 15일, 현재 결재 진행 중인 휴가일 12(4, 4, 4)일, 신청한 휴가일 endDayOfMonth 일
        // 15 - (12 + 4) < 0 이므로 예외 발생

        //GIVEN
        LocalDateTime startDate = LocalDateTime.of(2024, 3, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 3, endDayOfMonth, 0, 0);
        VacationDuration vacationDuration = new VacationDuration(VacationType.MORE_DAY, startDate, endDate);

        Organization organization = CoreEntityFactory.defalutOrganization();
        MemberLeave memberLeave = CoreEntityFactory.defaultMemberLeave(organization); // memberId == requesterId
        VacationManager vacationManager = VacationManager.createVacation(vacationDuration, memberLeave);

        Vacation vacation1 = Vacation.builder()
                .vacationStatus(VacationStatus.APPROVED)
                .vacationDuration(
                        new VacationDuration(
                                VacationType.MORE_DAY,
                                LocalDateTime.of(2024, 4, 1, 0, 0),
                                LocalDateTime.of(2024, 4, 4, 0, 0)))
                .requesterId("T0001")
                .deducted(true)
                .build();

        Vacation vacation2 = Vacation.builder()
                .vacationStatus(VacationStatus.REQUEST)
                .vacationDuration(new VacationDuration(
                        VacationType.MORE_DAY,
                        LocalDateTime.of(2024, 5, 1, 0, 0),
                        LocalDateTime.of(2024, 5, 4, 0, 0)))
                .requesterId("T0001")
                .deducted(true)
                .build();

        Vacation vacation3 = Vacation.builder()
                .vacationStatus(VacationStatus.REQUEST)
                .vacationDuration(new VacationDuration(
                        VacationType.MORE_DAY,
                        LocalDateTime.of(2024, 6, 1, 0, 0),
                        LocalDateTime.of(2024, 6, 4, 0, 0)))
                .requesterId("T0001")
                .deducted(true)
                .build();

        List<Vacation> vacations = List.of(vacation1, vacation2, vacation3);

        //WHEN - THEN
        assertThatCode(() -> vacationManager.validateRemainingLeaveIsBiggerThanConfirmingVacationsAnd(vacations))
                .doesNotThrowAnyException();
    }

    // 인스턴스 생성 후, 필드들의 비즈니스적 올바름 검증

    // 상신 가능 여부 확인

    // 상신

    // 취소 (결재 취소)

    // 수정
}