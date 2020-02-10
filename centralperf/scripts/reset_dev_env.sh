#!/usr/bin/env bash

# Current script path
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
DOCKER_FILES_PATH=${DIR}/../src/main/docker

read -p "Are you sure you want to reset all containers / data (DB / Indexes) (y/N)?" choice
if [[ $choice =~ ^[Yy]$ ]]; then
  echo "Remove containers and data"
  echo "=========================="

  echo "- Removing containers"
  cd ${DOCKER_FILES_PATH}
  docker-compose down

  echo "- Removing volumes"
  rm -Rf ${DOCKER_FILES_PATH}/volumes/db/data ${DOCKER_FILES_PATH}/volumes/es/data

  echo "- Exit"
  echo "=========================="
else
  echo "Aborted"
fi
