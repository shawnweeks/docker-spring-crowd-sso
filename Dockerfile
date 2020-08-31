ARG BUILD_BASE_REGISTRY=registry.cloudbrocktec.com
ARG BUILD_BASE_IMAGE=redhat/ubi/ubi8
ARG BUILD_BASE_TAG=8.2

ARG BASE_REGISTRY=registry.cloudbrocktec.com
ARG BASE_IMAGE=apache/docker-tomcat
ARG BASE_TAG=9.0

# Compile Maven Artifacts.
FROM ${BUILD_BASE_REGISTRY}/${BUILD_BASE_IMAGE}:${BUILD_BASE_TAG} as build
RUN yum install -y java-1.8.0-openjdk-devel maven
COPY [ ".", "/build/" ]
WORKDIR "/build"
RUN mvn package


# Build Crowd SSO Image
FROM ${BASE_REGISTRY}/${BASE_IMAGE}:${BASE_TAG}
COPY --from=build [ "/build/target/spring-crowd-sso.war", "${CATALINA_HOME}/webapps/" ]