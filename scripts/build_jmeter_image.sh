#!/usr/bin/env bash

# Current script path
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

cd ${DIR}/../src/main/docker/jmeter/
# Load jMeter version from Docker Compose .env
source ${DIR}/../src/main/docker/.env
echo "Build jMeter Docker Image with version ${JMETER_VERSION}"
docker build -t jmeter --build-arg JMETER_VERSION=${JMETER_VERSION} .