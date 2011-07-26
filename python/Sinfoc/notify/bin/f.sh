#!/bin/sh

set +x

pwd_run=`pwd`
bin_path=`dirname $0`
cd $bin_path/..
NOTIFY_HOME=`pwd`
export NOTIFY_HOME
echo "SINFOC Home: ${NOTIFY_HOME}"

proxy_file="${NOTIFY_HOME}/bin/set_proxy.sh"
if [ -f "${proxy_file}" ]
then
	. ${proxy_file}
fi

export JAR_PATH="${NOTIFY_HOME}/lib"

cd ${NOTIFY_HOME}/fetion_py

python fetion.py
