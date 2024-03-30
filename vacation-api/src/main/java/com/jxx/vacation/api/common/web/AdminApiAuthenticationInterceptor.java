package com.jxx.vacation.api.common.web;

import com.jxx.vacation.api.member.application.AuthService;
import com.jxx.vacation.api.member.application.UserSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 어떻게 할지 고민중
 */
@Slf4j
@RequiredArgsConstructor
public class AdminApiAuthenticationInterceptor implements HandlerInterceptor {

    private final AuthService authService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // TODO 관리자 인증 구현
        log.info("관리자 한정 API 를 호출하였습니다.");
        UserSession userSession = authService.getUserSession(request);

        // 임시 관리자 ID
        if (!"U00001".equals(userSession.getMemberId())) {
            log.warn("admin api call by {}", userSession.getMemberId());
            return false;
        }

        return true;
    }
}
