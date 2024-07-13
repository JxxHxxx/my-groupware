package com.jxx.vacation.api.excel;

public class ExcelFileReadException extends RuntimeException {

    /**
     * 파일을 읽어야 하는 목적을 담은 필드
     */
    private final String purpose;

    public ExcelFileReadException(String purpose, String message) {
        super(message);
        this.purpose = purpose;
    }

    public ExcelFileReadException(String purpose, String message, Throwable cause) {
        super(message, cause);
        this.purpose = purpose;
    }


    public String purpose() {
        return purpose;
    }
}
