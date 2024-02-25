package com.jxx.vacation.core.vacation.infra;

import com.jxx.vacation.core.vacation.domain.entity.Vacation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VacationRepository extends JpaRepository<Vacation, Long> {

    List<Vacation> findAllByRequesterId(String requesterId);
}
