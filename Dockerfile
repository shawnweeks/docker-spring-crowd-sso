ARG BASE_REGISTRY=registry.cloudbrocktec.com
ARG BASE_IMAGE=apache/docker-tomcat
ARG BASE_TAG=9.0

FROM ${BASE_REGISTRY}/${BASE_IMAGE}:${BASE_TAG} as builder

USER root
RUN dnf install -y maven.noarch

WORKDIR /app

COPY ./src/ /app/src/
COPY ./pom.xml /app/

RUN mvn clean package

FROM ${BASE_REGISTRY}/${BASE_IMAGE}:${BASE_TAG}

COPY --from=builder --chown=tomcat:tomcat [ "/app/target/spring-crowd-sso.war", "${CATALINA_HOME}/webapps/" ]