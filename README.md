### 프로젝트 구조
프로젝트는 총 4개로 이루어진다. 이중 실행 가능한 애플리케이션은 3개이다.

- vacation-core-module   
휴가 서비스에 필요한 도메인, 도메인 서비스, 인프라를 구성한다.
- vacation-api-module   
휴가 서비스에 필요한 API를 구현한다. API는 크게 두 종류로 구분한다.   
*public* : 외부(서비스 사용자)에서 사용할 수 있다.   
*private* : 내부(관리자)에서 사용할 수 있다.
- vacation-messaging-module   
휴가 서비스와 연동된 3rd-party 서버와의 메시지 통신을 구현한다.
- vacation-batch-module
휴가 서비스에 필요한 배치를 구현한다.

### 설정 파일
설정 파일(application.yml, logback 설정 등)은 프로젝트 wiki 참고
