package com.jxx.groupware.api.member.listener;

import com.jxx.groupware.api.work.application.WorkTicketReceiveEvent;
import com.jxx.groupware.core.vacation.domain.entity.MemberLeave;
import com.jxx.groupware.core.vacation.domain.entity.Organization;
import com.jxx.groupware.core.vacation.infra.MemberLeaveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component // 등록해야 동작
@RequiredArgsConstructor
public class OrgMemberEventListener {

    private final MemberLeaveRepository memberLeaveRepository;

    @EventListener(value = WorkTicketReceiveEvent.class)
    public void listen(WorkTicketReceiveEvent event) {
        log.info("orgMemberValidateEvent Occur {}", event);
        String memberId = event.receiverId();
        MemberLeave memberLeave = memberLeaveRepository.findMemberWithOrganizationFetch(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 memberId:" + memberId));

        Organization organization = memberLeave.getOrganization();
        if (organization.isSyncOrganization(event.chargeCompanyId(), event.chargeDepartmentId())) {
            log.info("memberId:{} is synchronized org", memberId);
        } else {
            log.error("\n memberId:{} is belong in companyId:{} departmentId:{} \n" +
                    "charge companyId:{} departmentId:{}, this member has not qualifications", memberId,
                    event.receiverCompanyId(), event.receiverDepartmentId(), event.chargeCompanyId(), event.receiverDepartmentId());

            throw new MemberOrgConsistencyException("사용자" + memberId + " 는 해당 티켓을 접수할 권한이 없습니다");
        }
    }
}
