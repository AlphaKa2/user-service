# 자바 버전 선택
FROM openjdk:17-jdk-alpine

# 인자 값 받아 ENV로 저장
ARG CONFIG_ADDRESS_ARG
ENV CONFIG_ADDRESS=${CONFIG_ADDRESS_ARG}
# 작업 디렉토리 설정
WORKDIR /app/config-service

# gradle로 빌드된 jar파일을 현재 디렉토리에 복사
COPY build/libs/*.jar app.jar

# 포트 노출
EXPOSE 8001

# develop 프로필로 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=develop", "--spring.config.import=configserver:${CONFIG_ADDRESS}"]