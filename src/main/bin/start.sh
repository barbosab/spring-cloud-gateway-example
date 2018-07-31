#!/bin/sh

echo "gateway starting"

HOME="data/gateway"
echo $HOME

LANG=zh_CN.UTF-8
LC_ALL=zh_CN.UTF-8

LIB=${HOME}/lib
CONF=${HOME}/conf
CLASS_PATH=${CONF}:${LIB}/*
JVM_OPT="-Xmx3g -Xms3g"
MAIN_CLASS="net.youqu.micro.service.GatewayApplication"

java ${JVM_ENV} ${JVM_OPT} -cp ${CLASS_PATH} ${MAIN_CLASS}>> /dev/null 2>&1
echo "gateway end"
sleep 1