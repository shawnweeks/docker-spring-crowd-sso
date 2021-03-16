ARG REGISTRY
ARG REDHAT_VERSION=8.3
ARG TOMCAT_VERSION=9.0.41

FROM ${REGISTRY}/redhat/ubi/ubi8:${REDHAT_VERSION} as build

RUN yum install -y java-1.8.0-openjdk-devel maven.noarch

COPY ./src/ /app/src/
COPY ./pom.xml /app/

ENV JAVA_HOME=/usr/lib/jvm/java-1.8.0
WORKDIR /app
RUN mvn clean package

###############################################################################
FROM ${REGISTRY}/apache/tomcat:${TOMCAT_VERSION}

COPY --from=build --chown=1001:1001 [ "/app/target/spring-crowd-sso.war", "${TOMCAT_HOME}/webapps/" ]
