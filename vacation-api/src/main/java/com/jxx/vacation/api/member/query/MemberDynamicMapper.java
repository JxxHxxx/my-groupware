package com.jxx.vacation.api.member.query;

import com.jxx.vacation.api.member.dto.request.MemberSearchCondition;
import com.jxx.vacation.api.member.dto.response.MemberProjection;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemberDynamicMapper {
    List<MemberProjection> search(MemberSearchCondition searchCondition);
}
