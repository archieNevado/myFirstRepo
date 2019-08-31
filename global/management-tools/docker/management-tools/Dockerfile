ARG TOOLS_BASE_IMAGE_TAG
ARG TOOLS_BASE_IMAGE_REPO
FROM ${TOOLS_BASE_IMAGE_REPO}:${TOOLS_BASE_IMAGE_TAG}
ENV JAVA_HEAP=384m TOOLS_USER=admin TOOLS_PASSWORD=admin SKIP_CONTENT=false \
    FORCE_REIMPORT_THEMES=false FORCE_REIMPORT_CONTENT=false BLOB_STORAGE_URL="" CONTENT_ARCHIVE_URL="" THEME_ARCHIVE_URL=""
COPY --chown=coremedia:coremedia target/tools /coremedia/tools
COPY --chown=coremedia:coremedia src/docker /coremedia
RUN mkdir /coremedia/import && chown coremedia . /coremedia/import
# Windows compatibility step
RUN find /coremedia -type d -exec chmod 770 {} \; && \
    chmod +x tools/bin/cm entrypoint.sh import-themes use-remote-content-archive import-user import-content import-default-workflows \
             import-custom-workflow publish-content export-content

CMD ["../bin/sh"]
ENTRYPOINT ["./entrypoint.sh"]
