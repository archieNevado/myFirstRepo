#!/bin/bash

IBM_WCS_ENABLED=$( [[ "${COMPOSE_FILE}" = *"commerce-adapter-wcs"* ]] && echo true || echo false )
sed -i "s|\${IBM_WCS_ENABLED}|${IBM_WCS_ENABLED}|g" \
    /usr/share/nginx/html/index.html \
    /usr/share/nginx/html/assets/actuators.html

SAP_HYBRIS_ENABLED=$( [[ "${COMPOSE_FILE}" = *"commerce-adapter-hybris"* ]] && echo true || echo false )
sed -i "s|\${SAP_HYBRIS_ENABLED}|${SAP_HYBRIS_ENABLED}|g" \
    /usr/share/nginx/html/index.html \
    /usr/share/nginx/html/assets/actuators.html

SFCC_ENABLED=$( [[ "${COMPOSE_FILE}" = *"commerce-adapter-sfcc"* ]] && echo true || echo false )
sed -i "s|\${SFCC_ENABLED}|${SFCC_ENABLED}|g" \
    /usr/share/nginx/html/index.html \
    /usr/share/nginx/html/assets/actuators.html

ADAPTER_MOCK_ENABLED=$( [[ "${COMPOSE_FILE}" = *"commerce-adapter-mock"* ]] && echo true || echo false )
sed -i "s|\${ADAPTER_MOCK_ENABLED}|${ADAPTER_MOCK_ENABLED}|g" \
    /usr/share/nginx/html/index.html \
    /usr/share/nginx/html/assets/actuators.html

STUDIO_PACKAGES_PROXY_ENABLED=$( [[ "${COMPOSE_FILE}" = *"studio-packages-proxy"* ]] && echo true || echo false )
sed -i "s|\${STUDIO_PACKAGES_PROXY_ENABLED}|${STUDIO_PACKAGES_PROXY_ENABLED}|g" \
    /usr/share/nginx/html/index.html \
    /usr/share/nginx/html/assets/actuators.html

nginx -g "daemon off;"
