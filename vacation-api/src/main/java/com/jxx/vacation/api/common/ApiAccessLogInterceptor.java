package com.jxx.vacation.api.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Slf4j
public class ApiAccessLogInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        String remoteHost = request.getRemoteHost();

        Map<String, String[]> params = request.getParameterMap();
        StringBuilder param = createParamUri(params);
        log.info("[{}][{}][{}]", remoteHost, uri, param);
        return true;
    }
    private static StringBuilder createParamUri(Map<String, String[]> params) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String param : params.keySet()) {
            stringBuilder.append(param + "=");
            String[] values = params.get(param);
            int valueSize = values.length;
            for (int idx = 0; idx < valueSize; idx++) {
                stringBuilder.append(values[idx]);
                if (idx + 1 < valueSize) {
                    stringBuilder.append(",");
                }
            }
        }
        return stringBuilder;
    }
}
