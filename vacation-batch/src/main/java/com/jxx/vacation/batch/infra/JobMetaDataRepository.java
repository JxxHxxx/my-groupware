package com.jxx.vacation.batch.infra;

import com.jxx.vacation.batch.domain.JobMetaData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JobMetaDataRepository extends JpaRepository<JobMetaData, Integer> {

    Optional<JobMetaData> findByJobName(String jobName);

    @Query("select jm from JobMetaData jm " +
            "join fetch jm.jobParams ")
    List<JobMetaData> fetchAllWithJobParams();
}
