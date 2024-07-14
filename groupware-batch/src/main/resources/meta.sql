# 배치 잡 실행 조회 API 를 위한 인덱스 작업
CREATE INDEX JOB_EXEC_START_TIME_IDX USING BTREE ON jxx_vacation.batch_job_execution (START_TIME);
