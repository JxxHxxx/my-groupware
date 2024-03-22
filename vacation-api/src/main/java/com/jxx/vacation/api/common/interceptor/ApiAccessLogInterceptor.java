package com.jxx.vacation.api.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * 해당 로그는 콘솔이 아닌 파일에만 적재
 */
@Slf4j
public class ApiAccessLogInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.setAttribute("apiStartTime", System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String uri = request.getRequestURI();
        String remoteHost = request.getRemoteHost();
        String httpMethod = request.getMethod();
        int httpStatusCode = response.getStatus();
        Map<String, String[]> params = request.getParameterMap();
        StringBuilder param = createParamUri(params);
        Long processTime = System.currentTimeMillis() - (Long) request.getAttribute("apiStartTime");
        // log format [요청을 보낸 서버 HOST][API HTTP 메서드][API URI][API param][API HttpStatusCode][처리시간]
        log.info("[{}][{}][{}][{}][{}][{}ms]", remoteHost, httpMethod, uri, param, httpStatusCode, processTime);
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
            stringBuilder.append(" ");
        }
        return stringBuilder;
    }
}
