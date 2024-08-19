package com.jxx.groupware.api.work.query;

import com.jxx.groupware.api.work.dto.request.WorkTickSearchCond;
import com.jxx.groupware.api.work.dto.response.WorkTicketServiceResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WorkTicketMapper {

    List<WorkTicketServiceResponse> search(WorkTickSearchCond searchCond);
}
