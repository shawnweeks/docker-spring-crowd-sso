FROM tomcat:9.0

RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    python3 python3-jinja2 && \
    rm -rf /var/lib/apt/lists/*

# Copy WAR file.
COPY target/spring-crowd-sso.war /usr/local/tomcat/apps/

# Copy Keycloak SAML adapter valve dependencies.
COPY lib/* /tmp/lib/

COPY config/context.xml /usr/local/tomcat/conf/
COPY config/server.xml.j2 /opt/spring-crowd-sso/
# Todo: Figure out how we will manage SAML keys.
COPY config/samlKeystore.jks /opt/spring-crowd-sso/
COPY config/keycloak-saml.xml /opt/spring-crowd-sso/
COPY entrypoint.sh /
COPY entrypoint.py /
COPY entrypoint_helpers.py /

# Avoid clobbering existing jars.
RUN mv -n /tmp/lib/* /usr/local/tomcat/lib

ENTRYPOINT ["/entrypoint.sh"]