package com.jxx.groupware.batch.application.validator;


import com.jxx.groupware.batch.exception.JxxJobExecutionException;
import com.jxx.groupware.batch.job.parameters.JxxJobParameter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VacationEndEventJobParametersValidatorTest {


    @DisplayName("leave.adjust.job 잡 파라미터 검증 시 " +
            "jobName, run.id, executeDateTime 3가지 파라미터가 모두 할당되어 있다면" +
            "어떠한 예외도 던지지 않는다.")
    @Test
    void leave_adjust_job_parameter_validate_success_case() throws JobParametersInvalidException {
        //given
        JobParametersValidator parametersValidator = new VacationEndEventJobParametersValidator();
        //when
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addJobParameter(JxxJobParameter.JOB_PARMA_JOB_NAME.keyName(), new JobParameter("TEST_JOB_NAME", String.class));
        jobParametersBuilder.addJobParameter(JxxJobParameter.JOB_PARAM_RUN_ID.keyName(), new JobParameter("TEST_RUN_ID", String.class));
        jobParametersBuilder.addJobParameter(JxxJobParameter.JOB_PARAM_EXECUTE_DATE_TIME.keyName(), new JobParameter("2024-12-12 00:00:00", String.class));
        JobParameters jobParameters = jobParametersBuilder.toJobParameters();
        //then
        assertThatCode(() -> parametersValidator.validate(jobParameters))
                .doesNotThrowAnyException();
    }

    // 파라미터 테스트로 조금 더 정확도 높게 검증할 수 있을듯
    @DisplayName("leave.adjust.job 잡 파라미터 검증 시 " +
            "jobName, run.id, executeDateTime 3가지 중 한가지라도 없다면 " +
            "JxxJobExecutionException 예외가 발생한다.")
    @Test
    void leave_adjust_job_parameter_validate_fail_case() {
        //given
        JobParametersValidator parametersValidator = new VacationEndEventJobParametersValidator();

        //when executeDateTime 파라미터가 존재하지 않는 상황
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addJobParameter(JxxJobParameter.JOB_PARMA_JOB_NAME.keyName(), new JobParameter("TEST_JOB_NAME", String.class));
        jobParametersBuilder.addJobParameter(JxxJobParameter.JOB_PARAM_RUN_ID.keyName(), new JobParameter("TEST_RUN_ID", String.class));

        JobParameters jobParameters = jobParametersBuilder.toJobParameters();

        //then
        assertThatThrownBy(() -> parametersValidator.validate(jobParameters))
                .isInstanceOf(JxxJobExecutionException.class);
    }
}