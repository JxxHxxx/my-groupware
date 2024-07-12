package com.jxx.vacation.batch.job.vacation.status.config;

import com.jxx.vacation.batch.job.vacation.status.item.VacationItem;
import com.jxx.vacation.batch.job.vacation.status.processor.VacationOngoingProcessor;
import com.jxx.vacation.batch.job.vacation.status.reader.VacationItemRowMapper;
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

import static com.jxx.vacation.batch.GlobalJobConstant.VACATION_END_JOB_NAME;
import static com.jxx.vacation.batch.GlobalJobConstant.VACATION_START_JOB_NAME;
import static com.jxx.vacation.batch.job.parameters.JxxJobParameter.*;

/**
 * 휴가 시작 설정 배치
 * APPROVED -> ONGOING
 * <p>
 * 리팩토링 - 클래스명 의믜가 불분명 한듯... VacationStatusManage -> VacationStartEventJob
 */

@Slf4j
@Configuration
@RequiredArgsConstructor
public class VacationStartEventJobConfiguration {

    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    @Bean(name = VACATION_START_JOB_NAME)
    public Job vacationStartEventJob(JobRepository jobRepository) {
        return new JobBuilder(VACATION_START_JOB_NAME, jobRepository)
                .start(step(jobRepository))
                .build();
    }

    @Bean(name = "vacation.start.step")
    public Step step(JobRepository jobRepository) {
        return new StepBuilder("vacation.start.step", jobRepository)
                .<VacationItem, VacationItem>chunk(10, transactionManager)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(vacationItemWriter())
                .build();
    }

    @StepScope
    @Bean("vacationItemJdbcReader")
    public JdbcCursorItemReader<VacationItem> itemReader() {
        JobContext context = JobSynchronizationManager.getContext();
        String processDate = String.valueOf(context.getJobParameters().get(JOB_PARAM_PROCESS_DATE.keyName()));

        return new JdbcCursorItemReaderBuilder<VacationItem>()
                .fetchSize(100)
                .dataSource(dataSource)
                .sql("SELECT " +
                        "JVM.VACATION_ID , " +
                        "JVM.LEAVE_DEDUCT , " +
                        "JVM.CREATE_TIME , " +
                        "JVM.REQUESTER_ID , " +
                        "JVD.START_DATE_TIME , " +
                        "JVD.END_DATE_TIME, " +
                        "JVM.VACATION_TYPE , " +
                        "JVM.COMPANY_ID , " +
                        "JVM.VACATION_STATUS " +
                        "FROM JXX_VACATION_MASTER JVM " +
                        "JOIN JXX_VACATION_DURATION JVD ON JVM.VACATION_ID  = JVD.VACATION_ID " +
                        "WHERE START_DATE_TIME> ? " +
                        "AND START_DATE_TIME < DATE_ADD(?, INTERVAL 1 DAY)")
                .rowMapper(new VacationItemRowMapper())
                .name("vacationItemJdbcReader")
                .preparedStatementSetter(preparedStatement -> {
                    preparedStatement.setString(1, processDate);
                    preparedStatement.setString(2, processDate);
                })
                .build();
    }

    // 구현체를 반환하도록 해야 한다.
    // 그렇지 않으면 인터페이스의 메소드만 사용할 수 있다. 다른 리스너나 추가 메서드를 사용할 수 없다.
    @StepScope
    @Bean(name = "VacationItemProcessor")
    public VacationOngoingProcessor itemProcessor() {
        return new VacationOngoingProcessor();
    }

    @StepScope
    @Bean(name = "compositeVacationItemWriter")
    public CompositeItemWriter<VacationItem> compositeVacationItemWriter() {
        return new CompositeItemWriterBuilder<VacationItem>()
                .delegates(List.of(
                        vacationItemWriter(),
                        vacationStatusManageHistoryWriter()))
                .build();
    }

    @StepScope
    @Bean("vacationItemJdbcWriter")
    public JdbcBatchItemWriter<VacationItem> vacationItemWriter() {
        return new JdbcBatchItemWriterBuilder<VacationItem>()
                .dataSource(dataSource)
                .sql("UPDATE JXX_VACATION_MASTER JVM " +
                        "   SET VACATION_STATUS=:vacationStatus " +
                        "   WHERE JVM.VACATION_ID=:vacationId")
                .beanMapped()
                .build();
    }

    @StepScope
    @Bean("vacationStatusManageHistoryWriter")
    public JdbcBatchItemWriter<VacationItem> vacationStatusManageHistoryWriter() {
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
                ":requesterId," +
                ":vacationType," +
                ":vacationId," +
                ":vacationStatus)";

        return new JdbcBatchItemWriterBuilder<VacationItem>()
                .dataSource(dataSource)
                .sql(sql)
                .beanMapped()
                .build();
    }

}
