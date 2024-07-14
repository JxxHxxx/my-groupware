package com.jxx.groupware.messaging.infra.mapper;

import com.jxx.groupware.core.message.body.vendor.confirm.VacationConfirmUpdateContentModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ConfirmDocumentMapper {
    void updateContent(VacationConfirmUpdateContentModel updateContentModel);
}
