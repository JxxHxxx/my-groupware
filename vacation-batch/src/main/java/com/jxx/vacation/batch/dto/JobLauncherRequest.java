package com.jxx.vacation.batch.dto;

import lombok.Getter;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

@Getter
public class JobLauncherRequest {
    private String jobName;
    private Properties properties;

    public JobParameters getJobParameters() {
        Map<String, JobParameter<?>> jobParams = new LinkedHashMap<>();
        Set<String> propertyKeys = properties.stringPropertyNames();
        for (String key : propertyKeys) {
            jobParams.put(key, new JobParameter(properties.get(key), String.class));
        }

        return new JobParameters(jobParams);
    }

}
