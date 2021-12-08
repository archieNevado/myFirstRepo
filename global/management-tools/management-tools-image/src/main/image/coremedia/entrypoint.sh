#!/bin/sh
set -e
if [ "${DEBUG_ENTRYPOINT}" = "true" ]; then
  echo "[DOCKER ENTRYPOINT] - DEBUG_ENTRYPOINT detected, all commands will be printed"
  set -x
  WAIT_LOGGER_LEVEL=debug
fi
# this fixes the warning, that when spring transforms SPRING_PROFILES to spring.profiles
unset SPRING_PROFILES

./confd ../bin/true

if [ "${SKIP_CONTENT}" = "true" ]; then
  echo "[DOCKER ENTRYPOINT] - skipping entrypoint chain $@"
else
  echo "[DOCKER ENTRYPOINT] - starting entrypoint chain $@"
  export EXPORT_CONTENT_DIR=/coremedia/export
  export IMPORT_CONTENT_DIR=/coremedia/import/content
  export IMPORT_USERS_DIR=/coremedia/import/users
  # create dirs
  mkdir -p ${IMPORT_CONTENT_DIR} ${IMPORT_USERS_DIR}
  # WAIT_HOSTS and WAIT_PATHS are evaluated by docker-compose-wait into wait routines. For more information,
  # see https://github.com/ufoscout/docker-compose-wait
  if [ -n "${WAIT_HOSTS}" ] || [ -n "${WAIT_PATHS}" ]; then
    echo "[DOCKER ENTRYPOINT] - waiting for service dependencies..."
    /usr/bin/docker-compose-wait && exec ./"${@}"
  else
    exec ./"${@}"
  fi
fi
