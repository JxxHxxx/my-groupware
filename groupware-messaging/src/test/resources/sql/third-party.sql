-- 써드 파티 스키마 정의
CREATE TABLE `jxx_confirm_document_master` (
    `CONFIRM_DOCUMENT_PK` bigint NOT NULL AUTO_INCREMENT COMMENT '결재 문서 테이블 PK',
    `APPROVAL_LINE_LIFE_CYCLE` varchar(255) DEFAULT NULL COMMENT '결재선 상태',
    `CONFIRM_STATUS` varchar(255) NOT NULL COMMENT '결재 상태',
    `CREATE_SYSTEM` varchar(255) NOT NULL COMMENT '결재 데이터를 생성한 시스템',
    `CREATE_TIME` datetime(6) NOT NULL COMMENT '결재 문서 생성 시간',
    `CONFIRM_DOCUMENT_ID` varchar(255) NOT NULL COMMENT '결재 문서 ID',
    `DOCUMENT_TYPE` varchar(255) NOT NULL COMMENT '결재 양식 종류(ex 휴가, 구매 신청)',
    `COMPANY_ID` varchar(255) NOT NULL COMMENT '요청자 회사 ID',
    `DEPARTMENT_ID` varchar(255) NOT NULL COMMENT '요청자 부서 ID',
    `REQUESTER_ID` varchar(255) NOT NULL COMMENT '결재 요청자 ID',
    `CONFIRM_DOCUMENT_CONTENT_PK` bigint DEFAULT NULL COMMENT '결재 문서 본문',
    PRIMARY KEY (`CONFIRM_DOCUMENT_PK`),
    UNIQUE KEY `IDX_CONFIRM_DOCUMENT_ID` (`CONFIRM_DOCUMENT_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3;

-- jxx_approval.jxx_confirm_document_content_master definition

CREATE TABLE `jxx_confirm_document_content_master` (
   `CONFIRM_DOCUMENT_CONTENT_PK` bigint NOT NULL AUTO_INCREMENT COMMENT '결재 문서 테이블 PK',
   `CONTENTS` json DEFAULT NULL,
   PRIMARY KEY (`CONFIRM_DOCUMENT_CONTENT_PK`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb3;