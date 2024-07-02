package com.jxx.vacation.batch.infra;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JobCustomExplorer {

    private final JdbcTemplate jdbcTemplate;

    public List<Long> test() {
        return jdbcTemplate.execute("SELECT JOB_EXECUTION_ID FROM BATCH_JOB_EXECUTION", (PreparedStatementCallback<List<Long>>) ps -> {
            ResultSet rs = ps.executeQuery();
            List<Long> jobExecutionIds = new ArrayList<>();
            while (rs.next()) {
                jobExecutionIds.add(rs.getLong("JOB_EXECUTION_ID"));
            }
            return jobExecutionIds;
        });
    }
}
