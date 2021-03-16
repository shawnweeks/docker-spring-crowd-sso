ARG REGISTRY=registry.cloudbrocktec.com
ARG REDHAT_VERSION=8.3

FROM ${REGISTRY}/redhat/ubi/ubi8:${REDHAT_VERSION}

RUN yum install -y openssl-devel java-11-openjdk-devel maven.noarch
# RUN yum install -y openssl-devel java-1.8.0-openjdk-devel maven.noarch

# COPY ./src/ /app/src/
# COPY ./pom.xml /app/
COPY ./target/spring-crowd-sso.jar /app/target/
COPY ./entrypoint.sh /app/entrypoint.sh

ENV JAVA_HOME=/usr/lib/jvm/java-1.8.0
WORKDIR /app
# RUN mvn clean package

RUN chmod 755 /app/entrypoint.*

EXPOSE 8080

CMD ["/app/entrypoint.sh"]