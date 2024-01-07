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

### YML 설정
서비스가 정상적으로 작동하기 위해서는 총 4개의 WAS 서버를 구성해야 한다. 이 프로젝트를 통해서는 3개의 WAS를 구동할 수 있다.   
나머지 WAS는 [confirm-server](https://github.com/JxxHxxx/confirm-server) 프로젝트를 참고하면 된다.

Mysql 환경에서 안정적이며 다른 DB를 사용하기 위해서는 다음과 같은 조건이 필요하다.
- json 타입을 지원하는 DB여야 한다.

-  `messaing.yml` messaging.produce.select-sql 값 지정
```
produce 에서 consumer 로 보내기 위한 엔티티 한 줄을 읽은 SELECT Query를 지정한다. 이 값은 사용하는 DB 마다 차이가 있다.
아래는 예시다.
```

*MYSQL* 
```
SELECT * FROM JXX_MESSAGE_Q
WHERE MESSAGE_PROCESS_STATUS = 'SENT'
LIMIT 1;
```
*ORACLE*
```
SELECT * FROM JXX_MESSAGE_Q 
WHERE MESSAGE_PROCESS_STATUS = 'SENT' 
FETCH FIRST 1 ROW ONLY;
```

다음은 프로젝트 실행을 위한 `application.yml` 설정에 필요한 값들이다.

*vacation-api yml*
```
spring:
  datasource:
    url: 
    username: 
    password: 
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: 
      naming:
        physical-strategy: org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl

org:
  hibernate:
    envers:
      audit_table_suffix:

server:
  port: 8080
```

*vacation-messaging yml*
```
spring:
  datasource:
    url: 
    username: 
    password: 
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: update
      naming:
        physical-strategy: org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl

org:
  hibernate:
    envers:
      audit_table_suffix:

poller:
  interval: 50

server:
  port: 8090

3rd-party:
  datasource:
    approval:
      url: 
      username: 
      password: 
      driver-class-name: com.mysql.cj.jdbc.Driver

messaging:
  produce:
    select-sql:
```
