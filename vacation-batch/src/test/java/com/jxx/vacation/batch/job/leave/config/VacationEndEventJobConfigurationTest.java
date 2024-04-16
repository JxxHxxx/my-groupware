package com.jxx.vacation.batch.job.leave.config;


import com.jxx.vacation.batch.job.leave.item.LeaveItem;
import com.jxx.vacation.batch.job.leave.reader.LeaveItemReaderFactory;
import com.jxx.vacation.core.vacation.domain.entity.*;
import com.jxx.vacation.core.vacation.infra.MemberLeaveRepository;
import com.jxx.vacation.core.vacation.infra.OrganizationRepository;
import com.jxx.vacation.core.vacation.infra.VacationRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.jxx.vacation.batch.job.parameters.JxxJobParameter.JOB_PARAM_EXECUTE_DATE_TIME;
import static org.assertj.core.api.Assertions.*;

/**
 * 테스트 컨테이너 써야될 듯... h2 - mysql SQL 문법 호환 안되는 부분 존재
 */

@Slf4j
@SpringBootTest
@SpringBatchTest
class VacationEndEventJobConfigurationTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private MemberLeaveRepository memberLeaveRepository;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private VacationRepository vacationRepository;

    @BeforeEach
    void init() {
        Organization organization = new Organization(
                "O0001",
                "TJX",
                "T0001",
                "테스트부서",
                "TOP",
                "");

        MemberLeave memberLeave = MemberLeave.builder()
                .memberId("T0001")
                .name("나재헌")
                .experienceYears(1)
                .enteredDate(LocalDate.of(2023, 8, 16))
                .leave(new Leave(15F, 15F))
                .organization(organization)
                .build();

        Vacation vacation = Vacation.builder()
                .leaveDeduct(LeaveDeduct.DEDUCT)
                .vacationType(VacationType.MORE_DAY)
                .vacationStatus(VacationStatus.CREATE)
                .requesterId(memberLeave.getMemberId())
                .companyId(organization.getCompanyId())
                .build();

        organizationRepository.save(organization);
        MemberLeave savedMemberLeave = memberLeaveRepository.save(memberLeave);
        vacationRepository.save(vacation);

        log.info("=========================================");
        log.info("savedMemberLeave {}", savedMemberLeave);
        log.info("=========================================");
    }

    @Test
    void testJob() throws Exception {
        Job job = context.getBean("vacation.end.job", Job.class);
        jobLauncherTestUtils.setJob(job);

        JobParametersBuilder parametersBuilder = new JobParametersBuilder();
        parametersBuilder.addJobParameter(JOB_PARAM_EXECUTE_DATE_TIME.keyName(), new JobParameter<>("2024-04-12 00:00:00", String.class));
        JobParameters jobParameters = parametersBuilder.toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        assertThat("COMPLETED").isEqualTo(jobExecution.getExitStatus().getExitCode());
    }
}