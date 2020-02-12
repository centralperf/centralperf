#!/usr/bin/env bash

# Current script path
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

cd ${DIR}/../src/main/docker/
docker-compose up -d es kibana db