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
### 로그 설정

*로그 구성의 목적*
1. 운영 및 모니터링
- 애플리케이션 내에서 발생하는 이슈에 대응하기 위해 사용된다.
이외에도 로그를 구성해야 하는 이유는 다양하지만 위 목적을 위주로 구성하였다.

##### logback.xml, loback.properties 파일 위치
```
- vacation-api/src/main/resources/logback.xml logback 설정 파일
- vacation-api/src/main/resources/logback.properties logback 설정을 위한 변수 파일 - logback.properties 파일 내용은 생략
```

##### logback.xml
```
<?xml version="1.0" encoding="UTF-8"?>
<configuration scan="true" scanPeriod="10 seconds">
    <property resource="logback.properties"/>

    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <Pattern>%d{YYYY-MM-dd}T%d{HH:mm:ss.SSS} %highlight([%-5level]) %boldBlue([%15.15t]) %logger{36}[line:%L] - %msg%n</Pattern>
        </encoder>
    </appender>

    <!-- 로그 파일 정책 -->
    <appender name="CONSOLE_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 현재 로그의 위치 주의) 프로젝트 루트 기준으로 내리는게 아님 -->
        <file>${log.base.dir}/was/jxx-vacation.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <!-- 아래 경로에 1일 주기로 gzip 으로 압축한다. %i 는 파일의 인덱스로 maxFileSize를 넘어가여 롤링이 추가로 발생하면 증가 -->
            <fileNamePattern>${log.base.dir}/was/jxx-vacation-%d{yyyy-MM-dd}.%i.log.gz</fileNamePattern>
            <maxHistory>${log.max.history}</maxHistory>
            <maxFileSize>${log.max.file-size}</maxFileSize>
            <cleanHistoryOnStart>true</cleanHistoryOnStart>
        </rollingPolicy>

        <encoder>
            <Pattern>%d{HH:mm:ss.SSS} [%15.15t] [%-5level] %logger{36}[line: %L] - %msg%n</Pattern>
        </encoder>
    </appender>

    <appender name="API_URI" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 현재 로그의 위치 주의) 프로젝트 루트 기준으로 내리는게 아님 -->
        <file>${log.base.dir}/access/api.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!-- 아래 패턴에 따라 로그 파일이 어느 주기마다 저장될지 결정됨 -->
            <fileNamePattern>${log.base.dir}/access/api-%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>${log.max.history}</maxHistory>
            <maxFileSize>${log.max.file-size}</maxFileSize>
            <cleanHistoryOnStart>true</cleanHistoryOnStart>
        </rollingPolicy>
        <encoder>
            <Pattern>%d{HH:mm:ss.SSS} %msg%n</Pattern>
        </encoder>
    </appender>

    <!-- API 접근 로그 -->
    <logger name="com.jxx.vacation.api.common.ApiAccessLogInterceptor" level="info" additivity="false">
        <appender-ref ref="API_URI"/>
    </logger>
    <!-- WAS 전역 -->
    <logger name="com.jxx.vacation.api" level="info">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="CONSOLE_FILE"/>
    </logger>
</configuration>
```

설정을 변경하려면 아래 링크를 통해 Appender 메뉴얼을 살펴보는 것을 추천한다.   
[logback manual - Appenders](https://logback.qos.ch/manual/appenders.html)

### 휴가 신청 API guide
messaging 프로젝트에서 많은 양의 큐를 처리하는걸 보고 싶을 때, 사용하면 좋다.

사전 준비물 : JXX_MEMBER_LEAVE_MASTER, JXX_ORGANIZATION_MASTER 레코드 , POSTMAN

POSTMAN 간단 설명

아래 Pre-request Script 를 통해 요청에 보낼 변수를 만들 수 있다.   
![image](https://github.com/JxxHxxx/huga-gaza/assets/87173870/8c144791-02aa-4e3e-9227-303b4788ab76)

만든 변수를 활용해 API 를 호출 할 때 마다 requestBody 에 변경된 값이 들어가게 할 수 있다. 추가로 API 콜렉션 기능을 사용하면 지정한 만큼 API를 호출 할 수 있다.   
![image](https://github.com/JxxHxxx/huga-gaza/assets/87173870/3d61a726-0331-4c47-8d50-b4346a4b27c3)

##### 함수 설명

휴가 신청 시 요구되는 사용자ID 를 무작위하게 하기 위한 함수

```
const randomRequestId = function () {
    const minU = 1;
    const maxU = 132;
    const minSPY = 1;
    const maxSPY = 301;

    const prefix = Math.random() < 0.5 ? "U" : "SPY";
    if (prefix === "U") {
        const randomIdU = Math.floor(Math.random() * (maxU)) + minU;
        return prefix + randomIdU.toString().padStart(5, '0')
    }
    const randomIdSPY = Math.floor(Math.random() * (maxSPY)) + minSPY;
    return prefix + randomIdSPY.toString().padStart(5, '0');
} 

pm.environment.set("requesterId", randomRequestId());
```

휴가 시작일, 종료일을 설정하기 위한 함수

```
const randomDate = () => {
    const date = new Date();

    const adjustValue = Math.floor(Math.random() * 60); // 휴가 시작일을 무작위하게 하기 위한 조정 값
    date.setDate(date.getDate() + adjustValue);

    const copyNowYear = date.getFullYear();
    const copyNowDay = String(date.getDate()).padStart(2, '0');
    const copyNowDate = String(date.getMonth() + 1).padStart(2, '0');

    const startDate = `${copyNowYear}-${copyNowDate}-${copyNowDay}T00:00:00`;

    const interval = Math.floor(Math.random() * 5); // 휴가 종료일을 무작위하게 하기 위한 조정 값
    date.setDate(date.getDate() + interval);
    const year = date.getFullYear();
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');

    const endDate = `${year}-${month}-${day}T00:00:00`;
    return [startDate, endDate];
}

const [startDate, endDate] =  randomDate();
pm.environment.set("startDate", startDate);
pm.environment.set("endDate", endDate);

```
