package com.jxx.groupware.batch.infra;

import com.jxx.groupware.batch.dto.request.JobHistoryCond;
import com.jxx.groupware.batch.dto.response.JobHistoryResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface JobCustomMapper {

    List<JobHistoryResponse> findJobExecutionHistory(JobHistoryCond cond);
}
