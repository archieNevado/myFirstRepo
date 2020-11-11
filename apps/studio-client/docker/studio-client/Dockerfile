# builder stage to extract all necessary artifacts
ARG ALPINE_BASE_IMAGE_TAG
ARG ALPINE_BASE_IMAGE_REPO
ARG NGINX_BASE_IMAGE_TAG
ARG NGINX_BASE_IMAGE_REPO
FROM ${ALPINE_BASE_IMAGE_REPO}:${ALPINE_BASE_IMAGE_TAG} as builder
RUN apk add --no-cache unzip && rm -rf /var/cache/apk/*
COPY target/studio-resources.jar /tmp
RUN mkdir /tmp/studio-resources-exploded && \
    unzip -qq -n /tmp/studio-resources.jar -d /tmp/merged && \
    chmod -R 644 /tmp/merged && \
    find /tmp/merged/ -type d -exec chmod -R 755 {} \;

# copy static studio client files from builder stage
FROM ${NGINX_BASE_IMAGE_REPO}:${NGINX_BASE_IMAGE_TAG}
LABEL maintainer="CoreMedia AG <support@coremedia.com>"
# set this empty for http/1.1
ENV PROTOCOL=http2
HEALTHCHECK --start-period=10s --interval=30s --timeout=3s CMD CURL_ARG=$(test "$PROTOCOL" = "http2" && echo "--http2-prior-knowledge" || echo "") && curl ${CURL_ARG} -f http://localhost || exit 1
COPY --from=builder /tmp/merged/META-INF/resources /usr/share/nginx/html
COPY nginx-templates /etc/nginx/templates
