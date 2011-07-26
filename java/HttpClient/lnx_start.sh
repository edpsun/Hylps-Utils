#!/bin/sh

CLASSPATH="."

for fn in `ls ./lib`
do
        CLASSPATH="./lib/${fn}:${CLASSPATH}"
done

CLASSPATH="${CLASSPATH}"

export CLASSPATH

export JAVA_OPT="-DDEBUG_MODE=false \
 -DUSE_NOTIFY_FILTER=true \
 -DSCAN_TIME=8 \
 -DUSE_HTTP_PROXY=false \
 -DHTTP_PROXY_HOST=www-proxy.cn.oracle.com \
 -DHTTP_PROXY_PORT=80"

echo "${CLASSPATH}"
set -x
java  ${JAVA_OPT} com.hylps.tscan.WatchManager

