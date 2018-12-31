# 회의실 예약 Application

회의실과 날짜, 사용 시간을 입력하여 회의실을 예약하는 회의실 예약 Application

 - Web Container는 Spring Boot의 기본 Container인 Embedded-Tomcat을 사용하였습니다.
 - 저용량이면서 가볍고 빠른 H2를 In-memory db로 사용하였습니다.
 - Boiler Plate한 코드 작성을 줄이기 위해 JPA를 사용하기 위해 Spring-Data-JPA를 사용하였고,
   Annotation을 통해 Constructor 및 Getter, Setter등을 자동으로 생성해주는 Lombok을 사용하였습니다.
 - Frontend 구현은 React로 구현하였고, webpack을 통해 bundling하여 View 페이지를 출력할 수 있도록 하였습니다.

## Strategy
 - 30분 단위가 아닌 예약 요청 및 기타 Invalid한 요청에 대해서는 JSR Bean Validation을 사용하여 검증함
 - 1회성 예약 요청과 반복 예약 요청은 서로 다른 객체로 구현하지 않고, 1회성 예약을 반복 횟수가 없는 것으로 판단하여 동일한 객체로 다룸
 - 중첩 판단을 빠르게 하기위해 시작 날짜 및 종료 날짜 및 요일을 계산하여 저장하고, 신규 요청 시 시작 날짜, 종료 날짜, 요일, 시작 시간, 종료 시간을 기준으로 중첩 여부를 판단함.
 - 예약 생성 시 동시성을 위해 저장 후에 해당 시간에 다른 예약이 생성되어 충돌되는지를 검사하고, 다른 예약이 생성된 경우 Exception을 발생시켜 Rollback 되도록 함.

## Build
``` bash
mvnw package
```

## Run
``` bash
cd target
java -jar conference_room-1.0-SNAPSHOT.jar
```
