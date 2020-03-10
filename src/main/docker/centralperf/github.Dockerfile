FROM openjdk:8-jdk-alpine

ARG CENTRALPERF_VERSION

# Download Github release asset
RUN apk --no-cache add curl
ENV CENTRAL_PERF_JAR_URL="https://github.com/centralperf/centralperf/releases/download/${CENTRALPERF_VERSION}/centralperf-webapp-${CENTRALPERF_VERSION}.jar"
RUN curl -L ${CENTRAL_PERF_JAR_URL} --output /centralperf.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/centralperf.jar"]
