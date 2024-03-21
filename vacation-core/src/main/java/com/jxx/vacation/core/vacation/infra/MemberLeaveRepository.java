package com.jxx.vacation.core.vacation.infra;

import com.jxx.vacation.core.vacation.domain.entity.MemberLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberLeaveRepository extends JpaRepository<MemberLeave, Long> {

    Optional<MemberLeave> findMemberLeaveByMemberId(String memberId);
    Optional<MemberLeave> findByMemberId(String memberId);

    @Query("select m from MemberLeave m join fetch m.organization where m.memberId =:memberId")
    Optional<MemberLeave> findByMemberIdWithFetch(@Param("memberId") String memberId);

    @Query("select m from MemberLeave m join fetch m.organization o " +
            "where o.companyId =:companyId " +
            "and o.departmentId =:departmentId")
    List<MemberLeave> findDepartmentMembers(@Param("companyId") String companyId, @Param("departmentId") String departmentId);

    @Query ("select m from MemberLeave m join fetch m.organization o " +
            "where o.companyId =:companyId and m.memberId in (:membersId)")
    List<MemberLeave> findCompanyMembers(@Param("companyId") String companyId, @Param("membersId") List<String> membersId);
}
