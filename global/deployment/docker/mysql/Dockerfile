FROM mysql:8.0.25
COPY --chown=mysql:mysql initdb.d/* /docker-entrypoint-initdb.d/
COPY --chown=mysql:mysql conf.d/* /etc/mysql/conf.d/
ENV MYSQL_PASSWORD=secret
ENV MYSQL_ROOT_PASSWORD=secret
