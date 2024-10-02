package com.jxx.groupware.core.work.domain;

import com.jxx.groupware.core.work.domain.exception.WorkClientException;
import com.jxx.groupware.core.work.dto.TicketReceiver;
import lombok.extern.slf4j.Slf4j;

import static com.jxx.groupware.core.work.domain.WorkStatus.*;

/** <pre>
 * Do not Enroll Spring Bean,
 * @Field : workTicket - 쓰기 지연을 발생시키기에 영속성 컨텍스트 내에서 관리되는 엔티티여야 한다.
 * </pre>
 */
@Slf4j
public class WorkManager {
    private final WorkTicket workTicket;

    public WorkManager(WorkTicket workTicket) {
        this.workTicket = workTicket;
    }

    /** WRITE QUERY : JPA dirty checking
     * 접수자의 티켓 반려 API
     */
    public void rejectFromReceiver(String rejectReason, TicketReceiver ticketReceiver) {
        // 반려 가능한 작업 티켓 상태인지 검사
        if (!REJECT_FROM_CHARGE_POSSIBLE_GROUP.contains(workTicket.getWorkStatus())) {
            throw new WorkClientException("반려할 수 없는 티켓입니다.");
        };

        // 삭제 가능한 사람인지 검증
        if (!workTicket.isNotReceiverRequest(ticketReceiver)) {
            throw new WorkClientException("접수자가 아닌 사용자가 반려하려고 합니다.");
        };

        workTicket.rejectTicketFromReceiver(rejectReason);
    }
}
