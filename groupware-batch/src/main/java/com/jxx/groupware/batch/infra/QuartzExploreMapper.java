package com.jxx.groupware.batch.infra;

import com.jxx.groupware.batch.dto.response.CronTriggerResponse;
import com.jxx.groupware.batch.dto.response.JobSchedulingResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface QuartzExploreMapper {

    /** group name = spring job bean name**/
    CronTriggerResponse findByGroupName(@Param("triggerGroupName") String triggerGroupName);
    List<CronTriggerResponse> findAll();
    JobSchedulingResponse findSchedulingInformation(@Param("triggerName") String triggerName);
    List<JobSchedulingResponse> findAllSchedulingInformation();

}
