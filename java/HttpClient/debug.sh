#!/bin/sh

CLASSPATH="."

for fn in `ls ./lib`
do
        CLASSPATH="./lib/${fn}:${CLASSPATH}"
done

CLASSPATH="/home/esun/work_bench/apr_workspace/HttpClient/bin:${CLASSPATH}"
export CLASSPATH

export JAVA_OPT="-DDEBUG_MODE=true \
-DUSE_NOTIFY_FILTER=true \
-DSCAN_TIME=9 \
-DUSE_HTTP_PROXY=false \
-DHTTP_PROXY_HOST=www-proxy.us.oracle.com \
-DHTTP_PROXY_PORT=80"

echo "${CLASSPATH}"
set -x
#java -Dhttp.proxyHost=10.182.122.8  -Dhttp.proxyPort=80 com.hylps.TestNet


java  ${JAVA_OPT} com.hylps.tscan.WatchManager

