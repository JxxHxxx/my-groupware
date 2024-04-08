package com.jxx.vacation.batch.job.leave.config;

import com.jxx.vacation.batch.job.leave.item.LeaveItem;
import com.jxx.vacation.batch.job.leave.processor.LeaveItemValidateProcessor;
import com.jxx.vacation.batch.job.leave.reader.LeaveItemRowMapper;
import com.jxx.vacation.core.common.converter.LocalDateTimeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.JobContext;
import org.springframework.batch.core.scope.context.JobSynchronizationManager;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.support.builder.CompositeItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.List;

import static com.jxx.vacation.batch.job.parameters.JxxJobParameter.*;


/**
 * LeaveAdjust 잡은 3가지 데이터 쓰기 작업을 합니다.
 * 1. 휴가 상태 ONGOING -> COMPLETED (진행중인 휴가 -> 종료 상태로 변경)
 * 2. MemberLeave RemainingLeave 차감 (잔여일차 소진한 휴가만큼 차감)
 * 3. 2번에 따른 MemberLeave History 생성
 */

@Slf4j
@Configuration
@RequiredArgsConstructor
public class LeaveAdjustJobConfiguration {

    private static final String JOB_NAME = "leave.adjust.job";
    private static final Long EXECUTE_DATE_TIME_ADJUST_VALUE = -1l;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    @Bean(name = JOB_NAME)
    public Job leaveAdjustJob(JobRepository jobRepository) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .start(step(jobRepository))
                .build();
    }

    @Bean
    public Step step(JobRepository jobRepository) {
        return new StepBuilder("leave.adjust.step", jobRepository)
                .<LeaveItem, LeaveItem>chunk(100, transactionManager)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(compositeItemWriter())
                .build();
    }

    @StepScope
    @Bean(name = "leaveItemReader")
    public JdbcCursorItemReader<LeaveItem> itemReader() {
        String sql = "SELECT " +
                "JMLM.MEMBER_PK , " +
                "JMLM.REMAINING_LEAVE , " +
                "JMLM.TOTAL_LEAVE , " +
                "JMLM.NAME ," +
                "JMLM.MEMBER_ID ," +
                "JMLM.EXPERIENCE_YEARS ," +
                "JMLM.IS_ACTIVE AS 'MEMBER_ACTIVE', " +
                "JMLM.ENTERED_DATE , " +
                "JVM.VACATION_ID , " +
                "JVM.DEDUCTED , " +
                "JVM.VACATION_STATUS , " +
                "JVM.VACATION_TYPE , " +
                "JVM.START_DATE_TIME , " +
                "JVM.END_DATE_TIME , " +
                "JOM.COMPANY_ID, " +
                "JOM.DEPARTMENT_ID , " +
                "JOM.IS_ACTIVE AS 'ORG_ACTIVE' FROM JXX_MEMBER_LEAVE_MASTER JMLM " +
                " JOIN JXX_ORGANIZATION_MASTER JOM " +
                " ON JMLM.COMPANY_ID = JOM.COMPANY_ID AND JMLM.DEPARTMENT_ID = JOM.DEPARTMENT_ID " +
                " JOIN JXX_VACATION_MASTER JVM " +
                " ON JMLM.MEMBER_ID = JVM.REQUESTER_ID " +
                " WHERE JVM.END_DATE_TIME = ? ;";

        JobContext context = JobSynchronizationManager.getContext();

        String executeDateTime = String.valueOf(context.getJobParameters().get(JOB_PARAM_EXECUTE_DATE_TIME.keyName()));
        String endDateTime = LocalDateTimeConverter.adjustDateTime(executeDateTime, EXECUTE_DATE_TIME_ADJUST_VALUE);

        return new JdbcCursorItemReaderBuilder<LeaveItem>()
                .name("leaveItemReader")
                .dataSource(dataSource)
                .fetchSize(3)
                .sql(sql)
                .rowMapper(new LeaveItemRowMapper())
                .preparedStatementSetter(ps -> ps.setString(1, endDateTime))
                .build();
    }

    @StepScope
    @Bean(name = "leaveItemProcessor")
    public LeaveItemValidateProcessor itemProcessor() {
        return new LeaveItemValidateProcessor();
    }

    @StepScope
    @Bean(name = "leaveAdjustWriter")
    public JdbcBatchItemWriter<LeaveItem> leaveAdjustWriter() {
        String sql = "UPDATE JXX_MEMBER_LEAVE_MASTER JLM" +
                "   SET JLM.REMAINING_LEAVE = JLM.REMAINING_LEAVE -:deductedAmount  " +
                "   WHERE JLM.MEMBER_PK =:memberPk ";

        return new JdbcBatchItemWriterBuilder<LeaveItem>()
                .dataSource(dataSource)
                .sql(sql)
                .beanMapped()
                .build();
    }

    @StepScope
    @Bean(name = "leaveHistoryWriter")
    public JdbcBatchItemWriter<LeaveItem> leaveHistoryWriter() {
        String sql = "INSERT INTO JXX_MEMBER_LEAVE_HIST " +
                "(EXECUTE_TIME, " +
                "EXECUTOR, " +
                "TASK_TYPE, " +
                "ENTERED_DATE, " +
                "EXPERIENCE_YEARS, " +
                "IS_ACTIVE, " +
                "REMAINING_LEAVE, " +
                "TOTAL_LEAVE, " +
                "MEMBER_ID, " +
                "MEMBER_PK, " +
                "NAME, " +
                "COMPANY_ID, " +
                "DEPARTMENT_ID)" +
                "VALUES (CURRENT_TIMESTAMP , " +
                "'BATCH', " +
                "'U', " +
                ":enteredDate, " +
                ":experienceYears, " +
                ":memberActive, " +
                ":remainingLeave -:deductedAmount, " +
                ":totalLeave, " +
                ":memberId, " +
                ":memberPk, " +
                ":name, " +
                ":companyId, " +
                ":departmentId);";

        return new JdbcBatchItemWriterBuilder<LeaveItem>()
                .dataSource(dataSource)
                .sql(sql)
                .beanMapped()
                .build();
    }

    @StepScope
    @Bean(name = "vacationStatusChangeWriter")
    public JdbcBatchItemWriter<LeaveItem> vacationStatusChangeWriter() {
        String sql = "UPDATE JXX_VACATION_MASTER JVM" +
                "   SET JVM.VACATION_STATUS =:vacationStatus  " +
                "   WHERE JVM.VACATION_ID =:vacationId ";

        return new JdbcBatchItemWriterBuilder<LeaveItem>()
                .dataSource(dataSource)
                .sql(sql)
                .beanMapped()
                .build();
    }

    @StepScope
    @Bean(name = "compositeLeaveItemWriter")
    public CompositeItemWriter<LeaveItem> compositeItemWriter() {
        return new CompositeItemWriterBuilder<LeaveItem>()
                .delegates(
                        List.of(leaveAdjustWriter(),
                                leaveHistoryWriter(),
                                vacationStatusChangeWriter())
                )
                .build();
    }

}
