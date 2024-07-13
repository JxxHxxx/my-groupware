package com.jxx.vacation.batch.exception;

public class JxxJobExecutionException extends RuntimeException {
    private final String message;
    public JxxJobExecutionException(String message) {
        this.message = message;
    }
    public JxxJobExecutionException(String message, Throwable cause) {
        super(cause);
        this.message = message;
    }

    public String message() {
        return this.message;
    }
}
