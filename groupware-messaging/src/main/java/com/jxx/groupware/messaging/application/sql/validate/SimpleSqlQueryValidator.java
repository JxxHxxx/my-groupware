package com.jxx.groupware.messaging.application.sql.validate;

import com.jxx.groupware.messaging.application.sql.builder.InsertBuilderParameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
public class SimpleSqlQueryValidator implements SqlQueryValidator {

    @Override
    public boolean notValid(InsertBuilderParameter parameter) {
        List<String> columnNames = parameter.columnNames();
        Set<String> requestParamKeySet = parameter.requestParamKeySet();

        // 사이즈가 일치하는지
        if (!Objects.equals(columnNames.size(), requestParamKeySet.size())) {
            log.info("컬럼매핑 수와 요청 받은 파라미터 keySet 의 수가 일치하지 않습니다. \ncolumnName: {} requestParamKeySet:{}", columnNames, requestParamKeySet);
            return true;
        }

        // 사이즈가 일치하면서 모든 값이 동일한지
        if (!requestParamKeySet.containsAll(columnNames)) {
            log.error("요청 파라미터 KeySet 에 포함되지 않은 컬림매핑이 존재합니다 \ncolumnName: {} requestParamKeySet:{}", columnNames, requestParamKeySet);
            return true;
        }

        return false;
    }
}
