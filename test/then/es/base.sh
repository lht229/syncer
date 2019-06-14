#!/usr/bin/env bash


source ${UTIL_LIB}


logi "-----"
logi "Testing $0"
logi "-----"

# tables in mysql_test.sql
names="news correctness types"


function esAssert() {
    instance=$1
    db=$2
    table=$3

    all=`extractMySqlCount ${instance} ${db} ${table}`
    logi "[Sync input] -- ${db}.${table}: $all"
    c1=`extractESCount ${db} ${table}`
    logi "[Sync result] -- ${db}*.${table} in ES : $c1"
    if [[ ${c1} -ne "$all" ]];then
        loge "$table not right"
    fi

}


for (( i = 0; i < ${MYSQL_INSTANCE}; ++i )); do
    for table in ${names} ; do
        esAssert mysql_${i} test_${i} ${table}
    done
done

# tables in mysql_simple.sql
esAssert mysql_0 simple simple_type


logi "-----"
logi "Done $0"
logi "-----"