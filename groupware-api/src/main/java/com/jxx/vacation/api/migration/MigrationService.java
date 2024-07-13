package com.jxx.vacation.api.migration;

import org.springframework.stereotype.Service;


/**
 * 방식은 총 4가지
 * 1. 관리자 페이지에서 입력
 * 2. 파일로 이관
 * 3. Application layer API 로 이관 (상대 측에 준비되어 있어야 함)
 * 4. Database 연결해서 이관 (애플리케이션 실행 시, 커넥션 맺고 자동으로 등록되도록 해야할듯...?)
 */


@Service
public class MigrationService {

}
