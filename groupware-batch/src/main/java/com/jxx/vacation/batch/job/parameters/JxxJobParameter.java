package com.jxx.vacation.batch.job.parameters;


import java.util.List;

public enum JxxJobParameter {
    JOB_PARMA_JOB_NAME("jobName", true),
    JOB_PARAM_RUN_ID("run.id",  true),
    JOB_PARAM_EXECUTE_DATE_TIME("executeDateTime",true),
    JOB_PARAM_PROCESS_DATE("processDate", true);

    private final String keyName;
    private final boolean required;

    public static final List<String> LEAVE_ADJUST_REQUIRED_GROUP =
            List.of(JOB_PARMA_JOB_NAME.keyName, JOB_PARAM_RUN_ID.keyName, JOB_PARAM_EXECUTE_DATE_TIME.keyName);
    public static final List<String> VACATION_STATUS_MANAGE_REQUIRED_GROUP =
            List.of(JOB_PARMA_JOB_NAME.keyName, JOB_PARAM_RUN_ID.keyName, JOB_PARAM_PROCESS_DATE.keyName);

    JxxJobParameter(String keyName, boolean required) {
        this.keyName = keyName;
        this.required = required;
    }

    public String keyName() {
        return this.keyName;
    }

    public boolean required() {
        return this.required;
    }
}
