#!/bin/sh

WWW="WWW"
STOCK_HOME="/home/esun/work_bench/apr_workspace/JavaTools/test/res/stock"
#/home/esun/depot/share/vs/data/2011_q1
rm -rf ${WWW}

CLASSPATH="/home/esun/work_bench/apr_workspace/JavaTools/bin:/win/d/setup/javastuff/database/db4o/db4o-8.0/lib/db4o-8.0.184.15484-all-java5.jar"
java -cp ${CLASSPATH} edpsun.stock.basicdata.selector.BDA ${STOCK_HOME} ${WWW}

