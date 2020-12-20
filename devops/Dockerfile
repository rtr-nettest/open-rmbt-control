ARG TAG=jre-13.0.2_8-debianslim

FROM adoptopenjdk/openjdk13:${TAG}

ARG OPENPORT=8080
ARG TOMCAT_JAR=rtr-1.0-SNAPSHOT.jar
ARG APPLICATION_HOME=/app

RUN echo 'debconf debconf/frontend select teletype' | debconf-set-selections

RUN apt-get update && apt-get -y upgrade
RUN apt-get install -y dnsutils
RUN apt-get clean
RUN rm -rf                        \
    /var/lib/apt/lists/*          \
    /var/log/alternatives.log     \
    /var/log/apt/history.log      \
    /var/log/apt/term.log         \
    /var/log/dpkg.log

ENV container docker

RUN rm -f           \
    /etc/machine-id \
    /var/lib/dbus/machine-id

COPY ./docker-entrypoint.sh ./wait-for-it.sh /usr/bin/
RUN chmod +x /usr/bin/docker-entrypoint.sh && chmod +x /usr/bin/wait-for-it.sh

WORKDIR ${APPLICATION_HOME}
COPY ./${TOMCAT_JAR} ${TOMCAT_JAR}

ENV SPRING_PROFILE dev
ENV TOMCAT_JAR ${TOMCAT_JAR}
ENV APPLICATION_HOME ${APPLICATION_HOME}
ENV PORT ${OPENPORT}


EXPOSE ${OPENPORT}

ENTRYPOINT [ "docker-entrypoint.sh" ]
