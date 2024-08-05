package com.jxx.groupware.batch.job.leave.reader;

import com.jxx.groupware.batch.job.leave.item.LeaveItem;
import org.springframework.batch.core.scope.context.JobContext;
import org.springframework.batch.core.scope.context.JobSynchronizationManager;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;

import javax.sql.DataSource;

import static com.jxx.groupware.batch.job.parameters.JxxJobParameter.JOB_PARAM_EXECUTE_DATE_TIME;

/**
 * LeaveItem 필드 순으로 가져와야함
 */
public class LeaveItemReaderFactory {
    public JdbcCursorItemReader<LeaveItem> leaveItemReader(DataSource dataSource) {
        String sql = "SELECT " +
                "JMLM.MEMBER_PK , " +
                "JVM.CREATE_TIME , " +
                "JMLM.REMAINING_LEAVE , " +
                "JMLM.TOTAL_LEAVE , " +
                "JMLM.NAME ," +
                "JMLM.MEMBER_ID ," +
                "JMLM.EXPERIENCE_YEARS ," +
                "JMLM.IS_ACTIVE AS 'MEMBER_ACTIVE', " +
                "JMLM.ENTERED_DATE , " +
                "JVM.VACATION_ID , " +
                "JVM.LEAVE_DEDUCT , " +
                "JVM.VACATION_STATUS , " +
                "JVM.VACATION_TYPE , " +
                "JVD.START_DATE_TIME , " +
                "JVD.END_DATE_TIME , " +
                "JOM.COMPANY_ID, " +
                "JOM.DEPARTMENT_ID , " +
                "JOM.IS_ACTIVE AS 'ORG_ACTIVE', " +
                "JVD.USE_LEAVE_VALUE, " +
                "JVD.LAST_DURATION FROM JXX_MEMBER_LEAVE_MASTER JMLM " +
                " JOIN JXX_ORGANIZATION_MASTER JOM " +
                " ON JMLM.COMPANY_ID = JOM.COMPANY_ID AND JMLM.DEPARTMENT_ID = JOM.DEPARTMENT_ID " +
                " JOIN JXX_VACATION_MASTER JVM " +
                " ON JMLM.MEMBER_ID = JVM.REQUESTER_ID " +
                " JOIN JXX_VACATION_DURATION JVD " +
                " ON JVD.VACATION_ID = JVM.VACATION_ID " +
                " WHERE JVD.END_DATE_TIME > DATE_SUB(?, INTERVAL 1 DAY) AND JVD.END_DATE_TIME < ?;";

        JobContext context = JobSynchronizationManager.getContext();
        // 실행 시간을 파라미터로 받는다 예를 들어 2024-08-05 가 들어오면 2024-08-04 일의 휴가가 종료 처리 된다.
        String executeDateTime = String.valueOf(context.getJobParameters().get(JOB_PARAM_EXECUTE_DATE_TIME.keyName()));

        return new JdbcCursorItemReaderBuilder<LeaveItem>()
                .name("leaveItemReader")
                .dataSource(dataSource)
                .fetchSize(3)
                .sql(sql)
                .rowMapper(new LeaveItemRowMapper())
                .preparedStatementSetter(ps -> {
                    ps.setString(1, executeDateTime);
                    ps.setString(2, executeDateTime);
                })
                .build();
    }
}
