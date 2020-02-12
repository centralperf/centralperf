#!/usr/bin/env bash

# Current script path
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

TIMESTAMP=$(date +%Y%m%d_%H%M%S)
HOST_SCRIPT_PATH=${DIR}/../src/main/resources/scripts_samples/
CONTAINER_PATH=/mnt/jmeter

docker run \
  --volume "${HOST_SCRIPT_PATH}":"${CONTAINER_PATH}" \
  jmeter \
  -n \
  -t ${CONTAINER_PATH}/cp_sample_script.jmx \
  -l /tmp/result_${TIMESTAMP}.jtl \
  -j /tmp/jmeter_${TIMESTAMP}.log