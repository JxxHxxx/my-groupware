package com.jxx.groupware.api.common.web;

import com.jxx.groupware.api.member.application.AuthService;
import com.jxx.groupware.api.member.application.UserSession;
import com.jxx.groupware.core.vacation.domain.exeception.AuthClientException;
import com.jxx.groupware.core.vacation.domain.exeception.AuthorizationException;
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
        UserSession userSession = authService.getUserSession(request);
        log.info("관리자 API 호출 : 사용자 {} 관리자 권한을 검증합니다.", userSession.getMemberId());

        // 임시 관리자 ID
        if (!"manager".equals(userSession.getMemberId())) {
            log.warn("관리자가 아닌 사용자가 관리자 API에 접근을 시도했습니다. 접근 시도자 ID:{}", userSession.getMemberId());
            throw new AuthorizationException("요청에 대한 권한이 존재하지 않습니다");
        }
        return true;
    }
}
