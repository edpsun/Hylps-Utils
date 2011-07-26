#!/bin/sh
#export INCLUDE_ALL=true
#export NO_CANNON=true

usage()
{
	echo "$0 -adst"
	echo "   -a -DINCLUDE_ALL=true"
	echo "   -d debug as true"
	echo "   -s data path "
	echo "   -t result path"	
}

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

echo "exec_home:${exec_home}"
echo "work_dir :${work_dir}"
echo "=========================="

java_option=""
debug=""

while getopts :ads:t: option
do
	case "$option" in
  	a)
  		java_option="-DINCLUDE_ALL=true"
	;;
	d)
  		debug="true"
	;;
  	s)
		data_path="$OPTARG"
  	;;
  	t)
		target_path="$OPTARG"
  	;;
  	?)
		echo "[Error] Usage error. "
   	;;
  	*)
		echo "[Error] Not a known option:$option."
   	;;
  	esac
done

if [ "X${data_path}" = "X" -o ! -d ${data_path} ]
then
	echo "[Error] Can not read data path"
	usage
	exit 1
fi

SBDA_CLASSPATH="${exec_home}/lib/superBDA.jar:${exec_home}/lib/db4o-all.jar"
SBDA_MAIN_CLASS="edpsun.stock.basicdata.selector.BDA"
echo java  ${java_option} -cp $SBDA_CLASSPATH ${SBDA_MAIN_CLASS} ${data_path} ${target_path:=superBDA_WORK_HOME}
java  ${java_option} -cp $SBDA_CLASSPATH ${SBDA_MAIN_CLASS} ${data_path} ${target_path:=superBDA_WORK_HOME}

cp -rf ${exec_home}/res ${target_path}
