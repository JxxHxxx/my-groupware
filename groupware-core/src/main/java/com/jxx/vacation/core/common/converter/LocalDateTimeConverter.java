package com.jxx.vacation.core.common.converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 배치 잡 실행 시, 파라미터로 실행 시간이 들어가게 되는게 일반적이다.
 * 실제 쿼리에는 실행 시간 +- N일이 되야 하는 경우 및 타입을 String 으로 변환해야 하는 일이 있어 해당 기능을 추가한다.
 *
 * 2024-07-08 추가, 해당 클래스 사용 X
 */
public class LocalDateTimeConverter {

    public static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String adjustDateTime(String dateTime, Long days) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, DEFAULT_FORMATTER);

        if (days > 0l) {
            return String.valueOf(localDateTime.plusDays(days));
        }
        else if (days < 0l) {
            return String.valueOf(localDateTime.minusDays(Math.abs(days)));
        }
        else {
            return String.valueOf(localDateTime);
        }
    }

    public static String adjustDateTime(String dateTime) {
        return adjustDateTime(dateTime, 0l);
    }

    public static LocalDateTime valueOf(String value) {
        return LocalDateTime.parse(value, DEFAULT_FORMATTER);
    }

}
