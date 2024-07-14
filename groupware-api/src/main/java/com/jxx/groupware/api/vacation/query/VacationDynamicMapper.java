package com.jxx.groupware.api.vacation.query;

import com.jxx.groupware.core.vacation.projection.VacationProjection;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface VacationDynamicMapper {
    List<VacationProjection> search(VacationSearchCondition searchCond);
}
