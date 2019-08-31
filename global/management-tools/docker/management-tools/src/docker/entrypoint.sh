#!/bin/sh
set -e
if [ "${DEBUG_ENTRYPOINT}" = "true" ]; then
  echo "[DOCKER ENTRYPOINT] - DEBUG_ENTRYPOINT detected, all commands will be printed"
  set -x
fi

CMS_IOR_PROPERTY="cap.client.server.ior.url=${CAP_CLIENT_SERVER_IOR_URL:-http://content-management-server:8080/ior}"
sed -i "s|cap.client.server.ior.url=http://content-management-server:8080/ior|${CMS_IOR_PROPERTY}|" /coremedia/tools/properties/corem/capclient.properties

if [ "${SKIP_CONTENT}" = "true" ]; then
  echo "[DOCKER ENTRYPOINT] - skipping content import chain $@"
else
  echo "[DOCKER ENTRYPOINT] - starting content import chain $@"
  export EXPORT_CONTENT_DIR=/coremedia/export
  export IMPORT_CONTENT_DIR=/coremedia/import/content
  export IMPORT_USERS_DIR=/coremedia/import/users
  # create dirs
  mkdir -p ${IMPORT_CONTENT_DIR} ${IMPORT_USERS_DIR} ${EXPORT_CONTENT_DIR}
  exec ./${@}
fi
