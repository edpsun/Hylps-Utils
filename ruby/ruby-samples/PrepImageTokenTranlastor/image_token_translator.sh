#!/bin/sh
############################################################################
# Prep Image Token Translator.
# This script is used to translate the image tokens scanned from log file 
# into corresponding url. Currently it only supports Beta/Prod for NA realm.
# If there is anything wrong, feel free to contact me sunp@amazon.com.
# Author: Edward Sun
############################################################################

set +x
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

echo "*************************************************************************"
echo "WORK_DIR  : $work_dir"
echo "EXEC_HOME : $exec_home"
echo "*************************************************************************"

ruby ${exec_home}/script/image_token_scan.rb "$@"
if [ $? = 0 ] 
then
	echo "Done! :)"
else
	echo "Error(s) encountered! :("
fi
