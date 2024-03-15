package com.jxx.vacation.api.member.application;


import com.jxx.vacation.api.member.dto.request.AuthenticationRequest;
import com.jxx.vacation.api.member.dto.request.LoginRequest;
import com.jxx.vacation.api.member.presentation.UnAuthenticationException;
import com.jxx.vacation.core.vacation.domain.entity.MemberLeave;
import com.jxx.vacation.core.vacation.domain.entity.Organization;
import com.jxx.vacation.core.vacation.domain.exeception.AuthClientException;
import com.jxx.vacation.core.vacation.infra.MemberLeaveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberLeaveRepository memberLeaveRepository;

    public LoginResponse signIn(LoginRequest loginRequest) {
        String memberId = loginRequest.memberId();
        MemberLeave memberLeave = memberLeaveRepository.findByMemberIdWithFetch(memberId)
                .orElseThrow(() -> {
                    log.info("[AUTH][존재하지 않는 사용자:{}]", memberId);
                    return new AuthClientException("로그인 오류");
                });

        // 임시 패스워드 검증 로직
        if (!memberLeave.getMemberId().equals(loginRequest.password())) {
            throw new AuthClientException("아이디/비밀번호 올바르지 않음 requester:" + memberId, memberId);
        };

        Organization organization = memberLeave.getOrganization();

        return new LoginResponse(memberLeave.receiveCompanyId(),
                organization.getCompanyName(),
                memberLeave.getMemberId(),
                memberLeave.getName(),
                organization.getDepartmentId(),
                organization.getDepartmentName());
    }


    public void validateUserSession(UserSession session, AuthenticationRequest request) {
        boolean memberIdEqual = Objects.equals(session.getMemberId(), request.memberId());
        boolean departmentIdEqual = Objects.equals(session.getDepartmentId(), request.departmentId());
        boolean companyIdEqual = Objects.equals(session.getCompanyId(), request.companyId());

        if (!(memberIdEqual && departmentIdEqual && companyIdEqual)) {
            log.warn("manipulated client request");
            throw new UnAuthenticationException();
        }
    }
}
