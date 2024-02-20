package com.jxx.vacation.core.common.generator;

public class ConfirmDocumentIdGenerator {

    private static final String ID_PREFIX = "VAC";
    private static final String DELIMITER = "";
    public static String execute(String companyId, Long vacationId) {
        return String.join(DELIMITER, ID_PREFIX, companyId, String.valueOf(vacationId));
    }
}
