package com.jxx.vacation.batch.infra;

import com.jxx.vacation.batch.dto.request.JobHistoryCond;
import com.jxx.vacation.batch.dto.response.JobHistoryResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface JobCustomMapper {

    List<JobHistoryResponse> findJobExecutionHistory(JobHistoryCond cond);
}
