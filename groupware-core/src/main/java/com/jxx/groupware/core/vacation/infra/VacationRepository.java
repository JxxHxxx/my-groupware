package com.jxx.groupware.core.vacation.infra;

import com.jxx.groupware.core.vacation.domain.entity.Vacation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VacationRepository extends JpaRepository<Vacation, Long> {

    List<Vacation> findAllByRequesterId(String requesterId);

    @Query(value = "select v from Vacation v " +
            "join fetch VacationDuration vd " +
            "where v.companyId=:companyId and v.vacationType ='COMMON_VACATION'")
    List<Vacation> findCommonVacation(@Param("companyId") String companyId);

    @Query(value = "select v from Vacation v " +
            "join fetch VacationDuration vd " +
            "where v.id=:vacationId ")
    List<Vacation> findWithVacationDurations(@Param("vacationId") Long vacationId);
}
