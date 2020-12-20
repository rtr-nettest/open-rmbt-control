#! /bin/bash

wait-for-it.sh ${DB_URI}:${DB_PORT} -t ${WAIT:-10} -s --
EXIT_CODE=$?
if [ ${EXIT_CODE} -ne 0 ]; then
    exit ${EXIT_CODE}
fi

cd ${APPLICATION_HOME}
$(which java) -jar ${JAVA_OPTS} \
    -Dspring.profiles.active="${SPRING_PROFILE}" \
    ${TOMCAT_JAR}
