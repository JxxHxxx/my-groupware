package com.jxx.vacation.batch.infra;

import com.jxx.vacation.batch.dto.response.CronTriggerResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface QuartzExploreMapper {

    /** group name = spring job bean name**/
    CronTriggerResponse findByGroupName(@Param("triggerGroupName") String triggerGroupName);
}
