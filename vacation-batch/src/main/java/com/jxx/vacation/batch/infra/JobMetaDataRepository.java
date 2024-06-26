package com.jxx.vacation.batch.infra;

import com.jxx.vacation.batch.domain.JobMetaData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobMetaDataRepository extends JpaRepository<JobMetaData, Integer> {

    Optional<JobMetaData> findByJobName(String jobName);
}
