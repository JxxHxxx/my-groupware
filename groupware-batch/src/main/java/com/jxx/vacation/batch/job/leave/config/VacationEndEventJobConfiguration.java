package com.jxx.vacation.batch.job.leave.config;

import com.jxx.vacation.batch.job.leave.item.LeaveItem;
import com.jxx.vacation.batch.job.leave.processor.LeaveItemValidateProcessor;
import com.jxx.vacation.batch.job.leave.reader.LeaveItemReaderFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.support.builder.CompositeItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.List;

import static com.jxx.vacation.batch.GlobalJobConstant.VACATION_END_JOB_NAME;


/**
 * LeaveAdjust 잡은 3가지 데이터 쓰기 작업을 합니다.
 * 1. 휴가 상태 ONGOING -> COMPLETED (진행중인 휴가 -> 종료 상태로 변경)
 * 2. MemberLeave RemainingLeave 차감 (잔여일차 소진한 휴가만큼 차감)
 * 3. 2번에 따른 MemberLeave History 생성
 *
 * 리팩토링 - VacationEndEventJob
 */

@Slf4j
@Configuration
@RequiredArgsConstructor
public class VacationEndEventJobConfiguration {
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    @Bean(name = VACATION_END_JOB_NAME)
    public Job VacationEndEventJob(JobRepository jobRepository) {
        return new JobBuilder(VACATION_END_JOB_NAME, jobRepository)
                .start(step(jobRepository))
                .build();
    }

    @Bean
    public Step step(JobRepository jobRepository) {
        return new StepBuilder("vacation.end.step", jobRepository)
                .<LeaveItem, LeaveItem>chunk(100, transactionManager)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(compositeItemWriter())
                .build();
    }

    @StepScope
    @Bean(name = "leaveItemReader")
    public JdbcCursorItemReader<LeaveItem> itemReader() {
        return new LeaveItemReaderFactory().leaveItemReader(dataSource);
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
                "   SET JLM.REMAINING_LEAVE = JLM.REMAINING_LEAVE -:useLeaveValue  " +
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
                ":remainingLeave -:useLeaveValue, " +
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
        String sql = "INSERT INTO JXX_VACATION_HIST " +
                "(COMPANY_ID, " +
                "CREATE_TIME, " +
                "EXECUTE_TIME, " +
                "EXECUTOR, " +
                "TASK_TYPE, " +
                "LEAVE_DEDUCT, " +
                "REQUESTER_ID, " +
                "VACATION_TYPE, " +
                "VACATION_ID, " +
                "VACATION_STATUS) VALUES " +
                "(:companyId," +
                ":createTime," + // 휴가 생성 시간 박아야됨 RowMapper 부터 변경해야함...
                "CURRENT_TIMESTAMP," +
                "'JXX-BATCH'," +
                "'U'," +
                ":leaveDeduct," +
                ":memberId," +
                ":vacationType," +
                ":vacationId," +
                ":vacationStatus)";

        return new JdbcBatchItemWriterBuilder<LeaveItem>()
                .dataSource(dataSource)
                .sql(sql)
                .beanMapped()
                .build();
    }

    @StepScope
    @Bean(name = "leaveAdjustHistoryWriter")
    public JdbcBatchItemWriter<LeaveItem> leaveAdjustHistoryWriter() {
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
                                vacationStatusChangeWriter(),
                                leaveAdjustHistoryWriter()))
                .build();
    }

}
