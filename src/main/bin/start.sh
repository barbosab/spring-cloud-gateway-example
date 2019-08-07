echo "gateway starting."

#export JAVA_HOME=/usr/local/jdk1.8.0_121
#export PATH=$JAVA_HOME/bin:$PATH
#export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar

#HOME=`pwd`
HOME="/data/gateway"
echo $HOME

LANG=zh_CN.UTF-8
LC_ALL=zh_CN.UTF-8

LIB=${HOME}/lib
CONF=${HOME}/conf
LOG_PATH=/data/logs/gateway

CLASS_PATH=${CONF}:${LIB}/*
JVM_OPT="-Xms8g -Xmx8g -Xmn1g -XX:MetaspaceSize=1024m -XX:MaxMetaspaceSize=2048m -XX:MaxDirectMemorySize=1g -XX:+ExplicitGCInvokesConcurrent -Dsun.rmi.dgc.server.gcInterval=2592000000 -Dio.netty.leakDetectionLevel=advanced -Dsun.rmi.dgc.client.gcInterval=2592000000 -XX:ParallelGCThreads=8 -Xloggc:/data/logs/gc.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/data/logs/java.hprof -Djava.awt.headless=true -Dsun.net.client.defaultConnectTimeout=10000 -Dsun.net.client.defaultReadTimeout=30000"
JVM_ENV="-DLOG_PATH=${LOG_PATH}/"
MAIN_CLASS="net.youqu.micro.service.GatewayApplication"

OP=$1
IN=$2
export LANG LC_ALL LOG_PATH

if [ ! -d $LOG_PATH ];then
    mkdir $LOG_PATH
fi
if [ ! -f ${LOG} ];then
    touch ${LOG}
fi

#java ${JVM_ENV} ${JVM_OPT} -cp ${CLASS_PATH} ${MAIN_CLASS} ${OP} ${IN}
java ${JVM_ENV} ${JVM_OPT} -cp ${CLASS_PATH} ${MAIN_CLASS} ${OP} ${IN} >> /dev/null 2>&1
echo "data increment end..."
sleep 1