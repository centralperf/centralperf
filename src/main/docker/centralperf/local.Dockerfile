FROM openjdk:8-jdk-alpine

ARG CENTRALPERF_VERSION
COPY /target/centralperf-webapp-${CENTRALPERF_VERSION}.jar /centralperf.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/centralperf.jar"]

