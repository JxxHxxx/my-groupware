package com.jxx.groupware.api.organization.query;

import com.jxx.groupware.api.organization.dto.request.OrganizationSearchCond;
import com.jxx.groupware.api.organization.dto.response.OrganizationServiceResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrganizationMapper {

    List<OrganizationServiceResponse> search(OrganizationSearchCond searchCond);
}
