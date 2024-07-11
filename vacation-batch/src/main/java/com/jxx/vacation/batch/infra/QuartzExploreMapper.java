package com.jxx.vacation.batch.infra;

import com.jxx.vacation.batch.dto.response.CronTriggerResponse;
import com.jxx.vacation.batch.dto.response.SchedulingResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface QuartzExploreMapper {

    /** group name = spring job bean name**/
    CronTriggerResponse findByGroupName(@Param("triggerGroupName") String triggerGroupName);
    List<CronTriggerResponse> findAll();
    SchedulingResponse findSchedulingInformation(@Param("triggerName") String triggerName);

}
