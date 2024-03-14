package com.jxx.vacation.api.member.presentation;

import com.jxx.vacation.api.member.application.AuthService;
import com.jxx.vacation.api.member.application.LoginResponse;
import com.jxx.vacation.api.member.dto.request.AuthenticationRequest;
import com.jxx.vacation.api.member.dto.request.LoginRequest;
import com.jxx.vacation.api.member.dto.response.LoginResult;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Objects;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthApiController {

    private final AuthService authService;

    private final static String SESSION_NAME = "jxx-c-id";

    @PostMapping("/api/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        LoginResponse serviceResponse = authService.login(loginRequest);

        HttpSession session = request.getSession();
        String sessionId = session.getId();

        session.setAttribute(sessionId, serviceResponse);
        Cookie cookie = new Cookie(SESSION_NAME, sessionId);
        cookie.setPath("/");
        response.addCookie(cookie);

        Object attribute = session.getAttribute(sessionId);
        log.info("session Info {}", attribute);

        return ResponseEntity.ok(new LoginResult<>(200, serviceResponse));
    }

    @GetMapping("/api/auth/check-authentication")
    public ResponseEntity<?> checkAuthentication(HttpServletRequest request) {
        Cookie sessionCookie = Arrays.stream(request.getCookies())
                .filter(cookie -> SESSION_NAME.equals(cookie.getName()))
                .findFirst()
                .orElseThrow(() -> new UnAuthenticationException());

        // 세션이 존재하지 않으면 미인증 대상이라 판단하고 예외
        String authSessionKey = sessionCookie.getValue();
        HttpSession session = request.getSession();
        Object sessionValue = session.getAttribute(authSessionKey);
        if (Objects.isNull(sessionValue)) {
            log.info("session Not Found");
            throw new UnAuthenticationException();
        }

        //이외 검증 로직 추가 가능...
        LoginResponse sessionResponse = (LoginResponse) sessionValue;
        log.info("session {}", sessionResponse);

        return ResponseEntity.ok(200);
    }

    @GetMapping("/api/v2/auth/check-authentication")
    public ResponseEntity<?> checkAuthenticationV2(@RequestBody AuthenticationRequest authenticationRequest, HttpServletRequest httpRequest) {
        Cookie sessionCookie = Arrays.stream(httpRequest.getCookies())
                .filter(cookie -> SESSION_NAME.equals(cookie.getName()))
                .findFirst()
                .orElseThrow(() -> new UnAuthenticationException());

        // 세션이 존재하지 않으면 미인증 대상이라 판단하고 예외
        String authSessionKey = sessionCookie.getValue();
        HttpSession session = httpRequest.getSession();
        Object sessionValue = session.getAttribute(authSessionKey);
        if (Objects.isNull(sessionValue)) {
            log.info("session Not Found");
            throw new UnAuthenticationException();
        }

        //이외 검증 로직 추가 가능...
        LoginResponse sessionResponse = (LoginResponse) sessionValue;
        authService.checkAuthentication(sessionResponse, authenticationRequest);
        log.info("session {}", sessionResponse);

        return ResponseEntity.ok(200);
    }

}
