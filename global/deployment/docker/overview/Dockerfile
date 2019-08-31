FROM nginx:1.15.12-alpine
COPY index.html /usr/share/nginx/html/
COPY assets /usr/share/nginx/html/assets
COPY entrypoint.sh /entrypoint.sh
COPY conf.d /etc/nginx/conf.d
RUN /bin/chmod 775 /entrypoint.sh
RUN apk add --no-cache bash
ENTRYPOINT ["/entrypoint.sh"]
