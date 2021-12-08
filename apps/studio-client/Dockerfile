# syntax=docker/dockerfile:1.3

ARG TYPE=zip
ARG STUDIO_CLIENT_TOOLING_IMAGE=scratch

# builder stage to extract all necessary artifacts
FROM alpine:3.9.4 as source-zip
ARG BUILD_TARGET_FOLDER=global/studio/build
ARG BUILD_TARGET_FILE=studio-client.studio-*.zip
ARG STUDIO_CLIENT_PACKAGE_PATH=${BUILD_TARGET_FOLDER}/${BUILD_TARGET_FILE}
COPY ${STUDIO_CLIENT_PACKAGE_PATH} /tmp/studio-client.studio.zip


FROM ${STUDIO_CLIENT_TOOLING_IMAGE} as source-build
WORKDIR /ws-root/apps/studio-client
COPY . /ws-root/apps/studio-client

# extensions
ARG EXTENSIONS_ZIP
RUN if [ -n "${EXTENSIONS_ZIP}" ] ; then \
    mkdir -p extensions/ && \
    sed -i 's#../../modules/extensions/#extensions/#g' /ws-root/apps/studio-client/pnpm-workspace.yaml  && \
    sed -i 's#../../modules/extensions/#extensions/#g' /ws-root/apps/studio-client/pnpm-lock.yaml && \
    unzip -oqq /ws-root/apps/studio-client/${EXTENSIONS_ZIP} -d extensions/ ; \
    fi

# install
ARG SKIP_GENERATE_LOCK_FILE=true
RUN --mount=type=secret,mode=0644,id=npmrc,target=/ws-root/apps/studio-client/.npmrc pnpm install --frozen-lockfile=${SKIP_GENERATE_LOCK_FILE}
## set build version
ARG BUILD_VERSION
RUN if [ -n "${BUILD_VERSION}" ] ; then pnpm run set-version -- ${BUILD_VERSION} ; else echo 'Skip set version.' ; fi
# build
RUN pnpm -r run build
# package
RUN pnpm -r run package --filter @coremedia-blueprint/studio-client.studio
RUN cp $(pnpm -r --filter @coremedia-blueprint/studio-client.studio exec pwd)/build/studio-client.studio-*.zip /tmp/studio-client.studio.zip


FROM source-${TYPE} as unzip-stage
RUN mkdir /app/ && \
    unzip -oqq /tmp/studio-client.studio.zip -d /app/ && \
    rm /tmp/studio-client.studio.zip


FROM nginx:1.21.3-alpine as final-stage
LABEL maintainer="CoreMedia GmbH <support@coremedia.com>"
# set this empty for http/1.1
ENV PROTOCOL=http2

HEALTHCHECK --start-period=10s --interval=30s --timeout=3s CMD CURL_ARG=$(test "$PROTOCOL" = "http2" && echo "--http2-prior-knowledge" || echo "") && curl ${CURL_ARG} -f http://localhost || exit 1
COPY --from=unzip-stage /app /coremedia/app
COPY nginx.conf.template /etc/nginx/templates/default.conf.template
RUN mkdir -p /coremedia/plugins
COPY entrypoint.sh /
RUN apk add --no-cache nodejs && \
    rm -rf /var/cache/apk/* && \
    chmod 775 /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]
CMD ["nginx", "-g", "daemon off;"]
