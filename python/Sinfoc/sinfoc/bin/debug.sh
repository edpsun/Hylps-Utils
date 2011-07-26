#!/bin/sh

set +x

pwd_run=`pwd`
bin_path=`dirname $0`
cd $bin_path/..
SINFOC_HOME=`pwd`
export SINFOC_HOME
echo "SINFOC Home: ${SINFOC_HOME}"

proxy_file="${SINFOC_HOME}/bin/set_proxy.sh"
if [ -f "${proxy_file}" ]
then
	. ${proxy_file}
fi

python ${SINFOC_HOME}/workman/workman.py -t StockInfo -c ${SINFOC_HOME}/conf/debug.conf $1
