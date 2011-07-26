#!/bin/sh

set +x

pwd_run=`pwd`
bin_path=`dirname $0`
cd $bin_path/..
NOTIFY_HOME=`pwd`
export SINFOC_HOME
echo "SINFOC Home: ${NOTIFY_HOME}"

proxy_file="${NOTIFY_HOME}/bin/lnx_set_proxy.sh"
if [ -f "${proxy_file}" ]
then
	. ${proxy_file}
fi


plan_id="$1"
mode="$2"

if [ "X${plan_id}" = "X" ] 
then 
	echo "[Error] NO plan id. Exit."
	exit -1
fi



if [ "X${mode}" = "X" ] 
then 
	mode="debug"
fi  

export PATH=/export/edward/software/python/bin:/export/edward/software/jdk/bin:${PATH}:/usr/local/bin

tmp_dir="/tmp"
tmp_file="${tmp_dir}/tmp_file"
app_site="http://edpsun.appspot.com"
url="${app_site}/wm?type=ParamControl&op=getconf"
wget "${url}" -q -O ${tmp_file}

conf_file="${NOTIFY_HOME}/conf/notify.conf"
rt_conf_file=${conf_file}.runtime
cp -f ${conf_file} ${rt_conf_file}

if [ -f "${tmp_file}" ]
then
	echo "[Note] add new param from appsite"
	echo "=================================="
	cat ${tmp_file} |grep 'conf-'|sed 's/conf-//g'
	cat ${tmp_file} |grep 'conf-'|sed 's/conf-//g' >> "${rt_conf_file}"
	echo ""
fi

python -u ${NOTIFY_HOME}/workman/workman.py -t Notify -c ${rt_conf_file} D:plan_id=${plan_id} D:mode=${mode}
