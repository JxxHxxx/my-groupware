package com.jxx.vacation.api.common.web;

import com.jxx.vacation.api.member.application.AuthService;
import com.jxx.vacation.api.member.application.UserSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Objects;

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
        String sessionKey = authService.getSessionKey(request);
        Object oUserSession = request.getSession(false).getAttribute(sessionKey);
        if (Objects.isNull(oUserSession)) {
            log.warn("유효하지 않은 세션값입니다.");
            return false;
        }

        UserSession userSession = (UserSession) oUserSession;
        // 임시 관리자 ID
        if (!"U00001".equals(userSession.getMemberId())) {
            log.warn("admin api call by {}", userSession.getMemberId());
            return false;
        }

        return true;
    }
}
