#!/usr/bin/env bash
SCRIPT=$(readlink -f $0)
SCRIPTPATH=`dirname $SCRIPT`

if [ "$#" -ne 1 ]
then
  echo "Usage: ./set-blueprint-version.sh <NEW VERSION>"
  exit 1
fi

set -eu
BASE_PATH=${SCRIPTPATH}/../../
NEW_VERSION=$1
OLD_VERSION=16-BP-SNAPSHOT

echo "set version in:"
# by using the version> within the pattern we make sure all version properties set to OLD_VERSION are not affected.
for i in `find ${BASE_PATH} -name pom.xml`; do
  if [ $(grep -c "<version>${OLD_VERSION}<" $i) -gt 0 ]; then
    echo "$i"
    sed -i "s#<version>${OLD_VERSION}<#<version>${NEW_VERSION}<#g" "$i"
    sed -i "s#<version>\[${OLD_VERSION}\]<#<version>\[${NEW_VERSION}\]<#g" "$i"
  fi
done
echo "set versions for cookbooks"
for i in `find ${BASE_PATH} -name "*.rb" -o -name "*.md" -name "*.json"`; do
  if [ $(grep -c "${OLD_VERSION}" $i) -gt 0 ]; then
    echo "$i"
    sed -i "s/${OLD_VERSION}/${NEW_VERSION}/g" "$i"
  fi
done
