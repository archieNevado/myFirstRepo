ARG JAVA_APPLICATION_BASE_IMAGE_TAG
ARG JAVA_APPLICATION_BASE_IMAGE_REPO
FROM ${JAVA_APPLICATION_BASE_IMAGE_REPO}:${JAVA_APPLICATION_BASE_IMAGE_TAG}
ENV JAVA_HEAP=1280m
EXPOSE 8080 8199
VOLUME /coremedia/cache
HEALTHCHECK --start-period=30s --interval=30s --timeout=3s CMD curl -Lf http://localhost:8080/blueprint/servlet/actuator/health || exit 1
COPY --chown=coremedia:coremedia src/docker /coremedia
COPY --chown=coremedia:coremedia target/cae-live-app.jar /coremedia/application.jar
# Windows compatibility step
RUN find /coremedia -type d -exec chmod 770 {} \; && \
    chmod +x enable-sitemap-generation

CMD ["application"]
