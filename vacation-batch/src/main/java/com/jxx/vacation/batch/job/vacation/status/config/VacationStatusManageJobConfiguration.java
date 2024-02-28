package com.jxx.vacation.batch.job.vacation.status.config;

import com.jxx.vacation.batch.job.vacation.status.item.VacationItem;
import com.jxx.vacation.batch.job.vacation.status.writer.VacationItemRowMapper;
import com.jxx.vacation.core.vacation.domain.entity.VacationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.JobContext;
import org.springframework.batch.core.scope.context.JobSynchronizationManager;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class VacationStatusManageJobConfiguration {

    private static final String JOB_NAME = "vacation.status-manage.job";
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    @Bean(name = JOB_NAME)
    public Job vacationStatusManageJob(JobRepository jobRepository) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .start(step(jobRepository))
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean(name = "vacation.status-manager.step")
    public Step step(JobRepository jobRepository) {
        return new StepBuilder("vacation.status-manager.step", jobRepository)
                .<VacationItem, VacationItem> chunk(10, transactionManager)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }

    @StepScope
    @Bean("vacationItemJdbcReader")
    public JdbcCursorItemReader<VacationItem> itemReader() {
        JobContext context = JobSynchronizationManager.getContext();
        String processDate = String.valueOf(context.getJobParameters().get("processDate"));

        return new JdbcCursorItemReaderBuilder<VacationItem>()
                .fetchSize(10)
                .dataSource(dataSource)
                .sql("SELECT * FROM JXX_VACATION_MASTER JVM " +
                    "   WHERE START_DATE_TIME = ?  AND VACATION_STATUS = 'CREATE'")
                .rowMapper(new VacationItemRowMapper())
                .name("vacationItemJdbcReader")
                .preparedStatementSetter(preparedStatement -> {
                    preparedStatement.setString(1, processDate);
                })
                .build();
    }

    @StepScope
    @Bean
    public ItemProcessor<VacationItem, VacationItem> itemProcessor() {
        return item -> { item.changeVacationStatus(VacationStatus.ONGOING);
            return item;
        };
    }

    @StepScope
    @Bean("vacationItemJdbcWriter")
    public JdbcBatchItemWriter<VacationItem> itemWriter() {
        return new JdbcBatchItemWriterBuilder<VacationItem>()
                .dataSource(dataSource)
                .sql("UPDATE JXX_VACATION_MASTER JVM " +
                        "   SET VACATION_STATUS=:vacationStatus ")
                .beanMapped()
                .build();
    }

}
