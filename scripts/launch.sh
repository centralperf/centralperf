#!/usr/bin/env bash

# Current script path
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

# For ElasticSearch, configure Max Map and volumes access rights
sysctl -w vm.max_map_count=262144
source ${DIR}/../src/main/docker/.env
if [[ ${ELASTIC_NODE_VOLUME_PATH} == .* ]]; then
  ELASTIC_NODE_VOLUME_ABS_PATH=${DIR}/../src/main/docker/${ELASTIC_NODE_VOLUME_PATH}
else
  ELASTIC_NODE_VOLUME_ABS_PATH=${ELASTIC_NODE_VOLUME_PATH}
fi
chmod g+rwx ${ELASTIC_NODE_VOLUME_ABS_PATH}
chgrp 0 ${ELASTIC_NODE_VOLUME_ABS_PATH}

cd ${DIR}/../src/main/docker/
docker-compose up -d --build