package com.jxx.vacation.core.vacation.infra;

import com.jxx.vacation.core.vacation.domain.entity.MemberLeave;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberLeaveRepository extends JpaRepository<MemberLeave, Long> {

    Optional<MemberLeave> findMemberLeaveByMemberId(String memberId);
    Optional<MemberLeave> findByMemberId(String memberId);

}
