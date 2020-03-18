#!/usr/bin/env bash

# Current script path
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
DEFAULT_CONF_FILE=${DIR}/../src/main/docker/.env
LOCAL_CONF_FILE=${DIR}/../centralperf.config

# For ElasticSearch, configure Max Map and volumes access rights
sysctl -w vm.max_map_count=262144
source ${DEFAULT_CONF_FILE}
if [[ ${ELASTIC_NODE_VOLUME_PATH} == .* ]]; then
  ELASTIC_NODE_VOLUME_ABS_PATH=${DIR}/../src/main/docker/${ELASTIC_NODE_VOLUME_PATH}
else
  ELASTIC_NODE_VOLUME_ABS_PATH=${ELASTIC_NODE_VOLUME_PATH}
fi
if [[ ! -d ${ELASTIC_NODE_VOLUME_PATH} ]]; then
  mkdir -p ${ELASTIC_NODE_VOLUME_ABS_PATH}
fi
chmod g+rwx ${ELASTIC_NODE_VOLUME_ABS_PATH}
chgrp 0 ${ELASTIC_NODE_VOLUME_ABS_PATH}

# Load local configuration
if [[ -f ${LOCAL_CONF_FILE} ]]; then
  echo "CENTRALPERF : Loading local configuration from file ${LOCAL_CONF_FILE}"
  source ${LOCAL_CONF_FILE}
else
  echo "CENTRALPERF : No local configuration found."
  echo "CENTRALPERF : Create ${LOCAL_CONF_FILE} file to override default configuration from ${DEFAULT_CONF_FILE}"
fi

echo "CENTRALPERF : Launch CentralPerf version '${CENTRALPERF_VERSION}'"

cd ${DIR}/../src/main/docker/
docker-compose up -d --build