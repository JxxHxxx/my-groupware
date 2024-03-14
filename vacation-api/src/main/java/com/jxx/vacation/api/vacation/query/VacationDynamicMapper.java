package com.jxx.vacation.api.vacation.query;

import com.jxx.vacation.core.vacation.projection.DepartmentVacationProjection;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface VacationDynamicMapper {
    List<DepartmentVacationProjection> search(VacationSearchCondition searchCond);
}
