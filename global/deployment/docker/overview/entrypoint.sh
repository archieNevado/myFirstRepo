#!/bin/bash

OPTIONAL_SERVICES=$(echo ${COMPOSE_FILE} | sed 's|\.yml||g' | sed 's|compose/||g')

sed -i "s|\${OPTIONAL_SERVICES}|${OPTIONAL_SERVICES}|g" \
    /usr/share/nginx/html/index.html \
    /usr/share/nginx/html/assets/actuators.html

nginx -g "daemon off;"
