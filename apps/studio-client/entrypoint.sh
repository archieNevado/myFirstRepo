#!/bin/sh
set -x
rm -rf /usr/share/nginx/html
cp -R /coremedia/app /usr/share/nginx/html
node /usr/share/nginx/html/install-packages.js /coremedia/plugins
rm /usr/share/nginx/html/install-packages.js*
./docker-entrypoint.sh "$@"
