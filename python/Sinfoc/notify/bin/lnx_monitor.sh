#!/bin/sh

set +x

#!/bin/sh

if [ -L $0 ]
then
        #spath=`ls -l $0 | sed 's|^.*-> *||g'`
        spath=`readlink $0`
        if [ ! -e "$spath" ]
        then
                s=`dirname $0`
                spath="$s/$spath"
        fi
else
        spath=$0
fi

exec_home=$(cd "$(dirname "$spath")"; pwd)
work_dir=`pwd`
work_dir=$(cd "$work_dir"; pwd)

echo "*********************************************"
echo "WORK_DIR  : $work_dir"
echo "EXEC_HOME : $exec_home"
echo "*********************************************"

cd $exec_home/..

if [ "${NP}" = "false" ]
then
	echo "no proxy"
else
	echo "set proxy"
	proxy_file="${exec_home}/lnx_set_proxy.sh"
	if [ -f "${proxy_file}" ]
	then
		. ${proxy_file}
	fi
fi

python -u workman/workman.py -t Monitor -c conf/monitor.conf D:VB=false $@
