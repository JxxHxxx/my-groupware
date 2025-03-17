### 애플리케이션 실행을 위한 설명
이 프로젝트는 총 4개의 모듈로 구성되어 있다.   
별개로 이 프로젝트와 Server to Server 통신을 하는 **전자결재** 프로젝트, **FE 프로젝트**가 별개로 존재한다.

**groupware-core-module**   
```
그룹웨어 서비스에 필요한 도메인, 도메인 서비스, 인프라를 구성한다.
```

**groupware-api-module**   
```
1. 그룹웨어 서비스 필요한 HTTP API를 구현한다. 
```

**groupware-messaging-module**  
```
그룹웨어 서비스와 연동된 3rd-party 서버와의 메시지 통신을 구현한다.
```

**groupware-batch-module**
```
그룹웨어 서비스에 필요한 배치를 구현한다.
```

[FE 프로젝트 바로가기](https://github.com/JxxHxxx/confirm-service-fe)   
- 프론트 개발이 처음이라 배워가면서 개발중이다.

[전자 결재 프로젝트 바로가기](https://github.com/JxxHxxx/confirm-server)   
- 결재선 지정, 상신, 결재 승인, 반려 등의 API를 호출할 수 있다.
- `vacation-messaging-module` 과 DB to DB 로 통신하여 데이터를 주고 받기에 온전한 사용을 위해서는 해당 프로그램이 실행된 상태여야 한다.

### 설정 파일
설정 파일(application.yml, logback 설정 등)은 프로젝트 wiki 참고
