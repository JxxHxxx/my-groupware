package com.jxx.groupware.api.member.listener;

import com.jxx.groupware.api.work.application.WorkTicketCreateEvent;
import com.jxx.groupware.api.work.application.WorkTicketReceiveEvent;
import com.jxx.groupware.core.vacation.domain.entity.MemberLeave;
import com.jxx.groupware.core.vacation.domain.entity.Organization;
import com.jxx.groupware.core.vacation.domain.exeception.OrganizationException;
import com.jxx.groupware.core.vacation.infra.MemberLeaveRepository;
import com.jxx.groupware.core.vacation.infra.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component // 등록해야 동작
@RequiredArgsConstructor
public class OrgMemberEventListener {

    private final MemberLeaveRepository memberLeaveRepository;
    private final OrganizationRepository organizationRepository;

    @EventListener(value = WorkTicketReceiveEvent.class)
    public void listen(WorkTicketReceiveEvent event) {
        String eventOccurMsg = "[WorkTicketReceiveEvent occurs]";

        String memberId = event.receiverId();
        MemberLeave memberLeave = memberLeaveRepository.findMemberWithOrganizationFetch(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 memberId:" + memberId));

        Organization organization = memberLeave.getOrganization();
        if (organization.isSyncOrganization(event.chargeCompanyId(), event.chargeDepartmentId())) {
            log.info("\n [SUCCESS] {} \n memberId:{} is synchronized org", eventOccurMsg, memberId);
        } else {
            log.error("\n [FAIL] {}  \n memberId:{} is belong in companyId:{} departmentId:{} \n" +
                    "charge companyId:{} departmentId:{}, this member has not qualifications", eventOccurMsg, memberId,
                    event.receiverCompanyId(), event.receiverDepartmentId(), event.chargeCompanyId(), event.receiverDepartmentId());

            throw new MemberOrgAuthenticationException("사용자" + memberId + " 는 해당 티켓을 접수할 권한이 없습니다");
        }
    }
    @EventListener(value = WorkTicketCreateEvent.class)
    public void listen(WorkTicketCreateEvent event) {
        String eventOccurMsg = "[WorkTicketCreateEvent occurs]";

        String companyId = event.chargeCompanyId();
        String departmentId = event.chargeDepartmentId();

        Optional<Organization> oOrganization = organizationRepository.findOrganizationByCompanyIdAndDepartmentId(companyId, departmentId);

        if (oOrganization.isEmpty()) {
            log.error("\n [FAIL] {} companyId:{} departmentId:{} doesn't exist ", eventOccurMsg, companyId, departmentId);
            throw new OrganizationException("존재하지 않는 부서 companyId:" + companyId + " departmentId:" + departmentId);
        }

        Organization organization = oOrganization.get();
        if (!organization.isActive()) {
            log.error("\n [FAIL] {} companyId:{} departmentId:{} is inactive", eventOccurMsg, companyId, departmentId);
            throw new OrganizationException("해당 부서는 비활성화 되어 있습니다. companyId:" + companyId + " departmentId:" + departmentId);
        }

        log.info("\n [SUCCESS] {} org companyId:{} departmentId:{}", eventOccurMsg, companyId, departmentId);
    }
}
