package com.jxx.vacation.batch.job.vacation.status.reader;

import com.jxx.vacation.batch.job.vacation.status.item.VacationItem;
import com.jxx.vacation.core.vacation.domain.entity.VacationStatus;
import com.jxx.vacation.core.vacation.domain.entity.VacationType;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class VacationItemRowMapper implements RowMapper<VacationItem> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public VacationItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        long vacationId = rs.getLong("VACATION_ID");
//        boolean deducted = rs.getBoolean("DEDUCTED");
        String leaveDeduct = rs.getString("LEAVE_DEDUCT");
        String requesterId = rs.getString("REQUESTER_ID");
        LocalDateTime startDateTime = LocalDateTime.parse(rs.getString("START_DATE_TIME"), formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(rs.getString("END_DATE_TIME"), formatter);
        VacationType vacationTypes = VacationType.valueOf(rs.getString("VACATION_TYPE"));
        String vacationStatus = rs.getString("VACATION_STATUS");
        return new VacationItem(vacationId, leaveDeduct, requesterId, startDateTime, endDateTime, vacationTypes, vacationStatus);
    }
}
