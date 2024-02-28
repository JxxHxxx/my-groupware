package com.jxx.vacation.batch.job.leave.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
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
public class LeaveAdjustJobConfiguration {

    private static final String JOB_NAME = "leave.adjust.job";
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    @Bean(name = JOB_NAME)
    public Job leaveAdjustJob(JobRepository jobRepository) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .start(step())
                .build();
    }

    @StepScope
    @Bean
    public Step step() {
        return new StepBuilder("leave.adjust.step")
                .<LeaveItem, LeaveItem>chunk(100, transactionManager)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }

    @StepScope
    @Bean
    public JdbcCursorItemReader<LeaveItem> itemReader() {
        return new JdbcCursorItemReaderBuilder<LeaveItem>()
                .dataSource(dataSource)
                .build();
    }

    @StepScope
    @Bean
    public ItemProcessor<LeaveItem, LeaveItem> itemProcessor() {
        return item -> {
            if (!item.memberOrgActive()) {
                return null;
            };
            if (!item.validateDeductAmount()) {
                return null;
            }
            return item;
        };
    }

    @StepScope
    @Bean
    public JdbcBatchItemWriter<LeaveItem> itemWriter() {
        return new JdbcBatchItemWriterBuilder<LeaveItem>()
                .build();
    }

}
