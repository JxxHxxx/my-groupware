package com.jxx.groupware.core.work.infra;

import com.jxx.groupware.core.work.domain.WorkDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkDetailRepository extends JpaRepository<WorkDetail, Long> {
}
