package com.jxx.vacation.core.vacation.infra;

import com.jxx.vacation.core.vacation.domain.entity.MemberLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberLeaveRepository extends JpaRepository<MemberLeave, Long> {

    Optional<MemberLeave> findMemberLeaveByMemberId(String memberId);
    Optional<MemberLeave> findByMemberId(String memberId);

    @Query("select m from MemberLeave m join fetch m.organization where m.memberId =:memberId")
    Optional<MemberLeave> findMemberWithOrganizationFetch(@Param("memberId") String memberId);

    @Query("select m from MemberLeave m join fetch m.organization o " +
            "where o.companyId =:companyId " +
            "and o.departmentId =:departmentId")
    List<MemberLeave> findDepartmentMembers(@Param("companyId") String companyId, @Param("departmentId") String departmentId);

    @Query ("select m from MemberLeave m join fetch m.organization o " +
            "where o.companyId =:companyId and m.memberId in (:membersId)")
    List<MemberLeave> findCompanyMembers(@Param("companyId") String companyId, @Param("membersId") List<String> membersId);


    /**
     * 주의, 네이티브 쿼리, 영속성 컨텍스트가 꼬일 수 있으니, 공동 연차 API 를 제외한 곳에서 호출하지 않는 것을 권장
     * flushAutomatically = true 인 이유는 Vacation 객체가 DB에 저장된 후, 아래 쿼리를 실행해야 비즈니스 흐름이 맞음
     *
     * @param leaveDate 차감 일 수
     * @param companyId 회사 식별자
     * @return update 된 레코드 수
     */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = "UPDATE JXX_MEMBER_LEAVE_MASTER MLM " +
            "SET MLM.REMAINING_LEAVE = MLM.REMAINING_LEAVE -:leaveDate " +
            "WHERE MLM.COMPANY_ID =:companyId " +
            "AND MLM.IS_ACTIVE = true", nativeQuery = true)
    int updateRemainingLeave(@Param("leaveDate") float leaveDate, @Param("companyId") String companyId);
}
