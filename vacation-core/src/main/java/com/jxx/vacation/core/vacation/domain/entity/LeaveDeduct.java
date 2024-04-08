package com.jxx.vacation.core.vacation.domain.entity;

public enum LeaveDeduct {
    PRE_DEDUCT("preDeduct", 10),
    DEDUCT("deduct", 20),
    NOT_DEDUCT("notDeduct",30);

    private final String value;
    private final int code;

    LeaveDeduct(String value, int code) {
        this.value = value;
        this.code = code;
    }

    public static boolean isLeaveDeductVacation(LeaveDeduct leaveDeduct) {
        return !LeaveDeduct.NOT_DEDUCT.equals(leaveDeduct);
    }
}
