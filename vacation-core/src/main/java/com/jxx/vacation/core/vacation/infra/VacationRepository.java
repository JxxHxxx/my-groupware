package com.jxx.vacation.core.vacation.infra;

import com.jxx.vacation.core.vacation.domain.entity.Vacation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VacationRepository extends JpaRepository<Vacation, Long> {

    List<Vacation> findAllByRequesterId(String requesterId);

    /**
     * SELECT JMLM.NAME, JVM.START_DATE_TIME , JVM.END_DATE_TIME, JVM.VACATION_TYPE , JVM.VACATION_STATUS
     * FROM JXX_VACATION_MASTER JVM
     * INNER JOIN JXX_MEMBER_LEAVE_MASTER JMLM ON JVM.REQUESTER_ID = JMLM.MEMBER_ID
     * WHERE REQUESTER_ID IN (
     * 	SELECT MEMBER_ID FROM JXX_MEMBER_LEAVE_MASTER
     * 	WHERE COMPANY_ID = 'SPY' AND DEPARTMENT_ID = 'SPY01001');
     */
    @Query("select v from Vacation v inner join MemberLeave m on v.requesterId = m.memberId " +
            "where v.requesterId in (select m2.memberId from MemberLeave m2 " +
            "where m2.organization.companyId =:companyId " +
            "and m2.organization.departmentId =:departmentId)")

    List<Vacation> findDepartmentVacation(@Param("companyId") String companyId, @Param("departmentId") String departmentId);
}
