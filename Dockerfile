ARG BASE_REGISTRY
ARG BASE_IMAGE=redhat/ubi/ubi8
ARG BASE_TAG=8.3

FROM ${BASE_REGISTRY}/${BASE_IMAGE}:${BASE_TAG} as build

RUN yum install -y java-1.8.0-openjdk-devel maven.noarch

COPY ./src/ /app/src/
COPY ./pom.xml /app/

ENV JAVA_HOME=/usr/lib/jvm/java-1.8.0
WORKDIR /app
RUN mvn clean package

###############################################################################
ARG BASE_REGISTRY
ARG BASE_IMAGE=apache/tomcat
ARG BASE_TAG=9.0.38

FROM ${BASE_REGISTRY}/${BASE_IMAGE}:${BASE_TAG}

COPY --from=build --chown=1001:1001 [ "/app/target/spring-crowd-sso.war", "${CATALINA_HOME}/webapps/" ]