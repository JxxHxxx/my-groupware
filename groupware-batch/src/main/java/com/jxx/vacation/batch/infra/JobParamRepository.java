package com.jxx.vacation.batch.infra;

import com.jxx.vacation.batch.domain.JobParam;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobParamRepository extends JpaRepository<JobParam, Integer> {
}
