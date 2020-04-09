#!/usr/bin/env bash

# Current script path
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

cd ${DIR}/../src/main/docker/jmeter/
# Load jMeter version from Docker Compose .env
source ${DIR}/../src/main/docker/.env
echo "Build jMeter Docker Image with version ${JMETER_VERSION}"

# Allows to set additional JVM_ARGS, especially for proxy settings if any. For example : ./build_jmeter_image.sh "-Dhttps.proxyHost=proxy.local -Dhttps.proxyPort=3128"
JVM_ARGS=""
if [[ -n "$1" ]]; then
  JVM_ARGS="$1"
fi
docker build -t centralperf_jmeter:latest --build-arg JMETER_VERSION="${JMETER_VERSION}" --build-arg JVM_ARGS="${JVM_ARGS}" .