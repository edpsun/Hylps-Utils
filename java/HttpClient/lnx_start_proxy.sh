#!/bin/sh

CLASSPATH="."

for fn in `ls ./lib`
do
        CLASSPATH="./lib/${fn}:${CLASSPATH}"
done


export CLASSPATH

export JAVA_OPT="-DDEBUG_MODE=false \
 -DUSE_NOTIFY_FILTER=true \
 -DSCAN_TIME=8 \
 -DUSE_HTTP_PROXY=true \
 -DHTTP_PROXY_HOST=www-proxy.us.oracle.com \
 -DHTTP_PROXY_PORT=80"

echo "${CLASSPATH}"
set -x
java  ${JAVA_OPT} com.hylps.tscan.WatchManager

