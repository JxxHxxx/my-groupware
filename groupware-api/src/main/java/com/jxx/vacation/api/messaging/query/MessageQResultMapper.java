package com.jxx.vacation.api.messaging.query;

import com.jxx.vacation.api.messaging.dto.request.MessageQResultSearchCondition;
import com.jxx.vacation.api.messaging.dto.response.MessageQResultResponse;
import com.jxx.vacation.api.messaging.dto.response.MessageQResultResponseV2;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageQResultMapper {

        List<MessageQResultResponseV2> search(MessageQResultSearchCondition condition);
}
