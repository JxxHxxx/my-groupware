package com.jxx.groupware.batch.infra;

import com.jxx.groupware.batch.domain.JobParam;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobParamRepository extends JpaRepository<JobParam, Integer> {
}
