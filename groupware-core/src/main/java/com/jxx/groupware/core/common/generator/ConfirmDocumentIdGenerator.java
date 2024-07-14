package com.jxx.groupware.core.common.generator;

public class ConfirmDocumentIdGenerator {

    private static final String ID_PREFIX = "VAC";
    private static final String DELIMITER = "";

    // 조립기
    public static String execute(String companyId, Long vacationId) {
        return String.join(DELIMITER, ID_PREFIX, companyId, String.valueOf(vacationId));
    }

    // 분리기
}
