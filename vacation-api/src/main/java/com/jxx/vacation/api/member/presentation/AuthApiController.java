package com.jxx.vacation.api.member.presentation;

import com.jxx.vacation.api.member.application.AuthService;
import com.jxx.vacation.api.member.application.LoginResponse;
import com.jxx.vacation.api.member.dto.request.LoginRequest;
import com.jxx.vacation.api.member.dto.response.LoginResult;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthApiController {

    private final AuthService authService;

    @PostMapping("/api/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        LoginResponse serviceResponse = authService.login(loginRequest);

        HttpSession session = request.getSession();
        String sessionId = session.getId();

        session.setAttribute(sessionId, serviceResponse);
        Cookie cookie = new Cookie("jxx-c-id", sessionId);
        cookie.setPath("/");
        response.addCookie(cookie);

        Object attribute = session.getAttribute(sessionId);
        log.info("session Info {}", attribute);

        return ResponseEntity.ok(new LoginResult<>(200, serviceResponse));
    }


}
