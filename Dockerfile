ARG BASE_REGISTRY=registry.cloudbrocktec.com
ARG BASE_IMAGE=apache/docker-tomcat
ARG BASE_TAG=9.0

FROM ${BASE_REGISTRY}/${BASE_IMAGE}:${BASE_TAG}
COPY --chown=tomcat:tomcat [ "target/spring-crowd-sso.war", "${CATALINA_HOME}/webapps/" ]