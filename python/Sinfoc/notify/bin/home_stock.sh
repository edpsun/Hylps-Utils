#!/bin/sh

set +x

pwd_run=`pwd`
bin_path=`dirname $0`
cd $bin_path/..
NOTIFY_HOME=`pwd`
export SINFOC_HOME
echo "NOTIFY Home: ${NOTIFY_HOME}"


plan_id="$1"
mode="$2"



python -u workman/workman.py -t Monitor -c conf/monitor.conf $@
