package com.jxx.groupware.batch.infra;

import com.jxx.groupware.batch.domain.JobMetaData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface JobMetaDataRepository extends JpaRepository<JobMetaData, Integer> {

    Optional<JobMetaData> findByJobName(String jobName);

    @Query("select jm from JobMetaData jm " +
            "join fetch jm.jobParams ")
    List<JobMetaData> fetchAllWithJobParams();

    @Query("select jm from JobMetaData jm " +
            "join fetch jm.jobParams " +
            "where jm.jobName =:jobName ")
    Optional<JobMetaData> fetchByJobName(String jobName);

}
