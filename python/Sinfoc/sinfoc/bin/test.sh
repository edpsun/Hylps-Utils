#!/bin/sh

set +x
cur_dir=`pwd`
test_home=`dirname $cur_dir`
echo "Test Home: ${test_home}"

echo "Test 1 ============================="
python ${test_home}/workman/workman.py -t Domain -s CreateDomain -w hello -c ${test_home}/conf/sinfoc.conf
echo "Test 2 ============================="
python ${test_home}/wm_test.py         -t Domain -s CreateDomain -w hello -c ${test_home}/conf/test.conf

cd ${test_home}
echo "Test 3 ============================="
python workman/workman.py -t Domain -s CreateDomain -w hello -c conf/test.conf
echo "Test 4 ============================="
python wm_test.py         -t Domain -s CreateDomain -w hello -c ./conf/test.conf

cd workman
echo "Test 5 ============================="
python workman.py -t Domain -s CreateDomain -w hello -c ../conf/test.conf