package com.jxx.vacation.core.vacation.domain.entity;


import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Slf4j
class MemberLeaveHistoryTest {

    @DisplayName("MemberLeaveHistory 엔티티가 MemberLeave 엔티티의 모든 필드를 가지고 있는지 검증한다." +
            "MemberLeave 엔티티의 pk -> MemberLeaveHistory memberPk 로 매핑된다.")
    @Test
    void validate_memberLeave_history_field() throws NoSuchFieldException {
        //given
        Class<MemberLeave> memberLeaveClazz = MemberLeave.class;
        List<String> memberLeaveFieldNames = Arrays
                .stream(memberLeaveClazz.getDeclaredFields())
                .map(field -> field.getName())
                .toList();

        Class<MemberLeaveHistory> memberLeaveHistoryClass = MemberLeaveHistory.class;

        for (String memberLeaveFieldName : memberLeaveFieldNames) {
            log.info("master field {}" , memberLeaveFieldName);
            String masterOriginalFieldName = memberLeaveFieldName; // 로그 확인을 위해
            if (memberLeaveFieldName.equals("pk")) {
                memberLeaveFieldName = "memberPk";
            }
            String finalMemberLeaveFieldName = memberLeaveFieldName;
            // 해당 필드를 가지는지
            assertThatCode(() -> memberLeaveHistoryClass.getDeclaredField(finalMemberLeaveFieldName))
                    .doesNotThrowAnyException();

            String historyFieldName = memberLeaveHistoryClass.getDeclaredField(finalMemberLeaveFieldName).getName();

            assertThat(historyFieldName).isEqualTo(finalMemberLeaveFieldName);
            log.info("MASTER:{} -> HISTORY:{}" , masterOriginalFieldName, historyFieldName);
        }
    }

}