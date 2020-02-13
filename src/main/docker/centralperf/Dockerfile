FROM openjdk:8-jdk-alpine

ARG CENTRALPERF_VERSION

# Download Github release asset
RUN apk --no-cache add curl
RUN curl https://github.com/centralperf/centralperf/releases/download/${CENTRALPERF_VERSION}/centralperf-webapp-${CENTRALPERF_VERSION}.jar --output /centralperf.jar

ENTRYPOINT ["java","-jar","/centralperf.jar"]

