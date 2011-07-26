#!/bin/sh
if [ $# -lt 1 ]
then
    echo "please give the log file name."
    echo "Usage: $0 YOUR_LOG_FILE_PATH"
    exit -1
fi

cp $1 ./log_all

#java weblogic.WLST SearchLog.py
python SearchLog.py

echo "Test Report." >>report.txt
echo "================================" >>report.txt
echo "[testClientSide]" >>report.txt
echo "----------------" >>report.txt
grep '] FAILURE' log_c.txt >>report.txt
grep '] SUCCESS' log_c.txt >>report.txt

echo "[testServerSide]" >>report.txt
echo "----------------" >>report.txt
grep '] FAILURE' log_s.txt >>report.txt
grep '] SUCCESS' log_s.txt >>report.txt

rm ./log_all

echo 'Process done'
echo "--------------"
echo "=>Client Side Log: log_c.txt"
echo "=>Server Side Log: log_s.txt"
echo "=>Test Report    : report.txt"