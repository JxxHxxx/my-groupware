package com.jxx.vacation.messaging.infra.mapper;

import com.jxx.vacation.core.message.body.vendor.confirm.VacationConfirmUpdateContentModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ConfirmDocumentMapper {
    void updateContent(VacationConfirmUpdateContentModel updateContentModel);
}
