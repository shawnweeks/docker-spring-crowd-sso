# Dockerized Spring-boot Crowd SSO Login App
Provides an OpenId based login point that can generate a Crowd SSO token.
Since Atlassian uses a cookie to track your SSO session, this application and the Atlassian tools must be presented to the user from the same domain. Examples "my-domain.com/jira" or "jira.my-domain.com".

Uses the keycloak-spring-boot-starter adapter.

## Build and Run

```shell
docker build \
    -t $REGISTRY/spring-crowd-sso:1.0.0 \
    --build-arg REGISTRY=$REGISTRY \
    .
    
docker push $REGISTRY/spring-crowd-sso
```

###  Example Run Command
```shell
docker run -it --rm --init --name='spring-crowd-sso' \
    -p 8080:8080 \
    -e CROWD_SERVER_URL='http://crowd:8095/crowd/services/' \
    -e CROWD_BASE_URL='http://crowd:8095/crowd/' \
    -e CROWD_APPLICATION_LOGIN_URL='http://crowd:8095/crowd/console/' \
    -e keycloak_realm='demo' \
    -e keycloak_public-client='true' \
    -e keycloak_auth-server-url='https://demo.cloudbrocktec.com/auth' \
    -e keycloak_ssl-required='external' \
    -e keycloak_resource='oidctest' \
    -e keycloak_use-resource-role-mappings='false' \
    -e keycloak_principal-attribute='preferred_username' \
    $REGISTRY/spring-crowd-sso:1.0.0
```

Example url to get crowd token.
https://demo.cloudbrocktec.com/spring-oidc/token

### Run Parameters
| Environment Variable | Description | Default|
| --- | --- | ---|
| server_port | | 8080 |
| server_forward-headers-strategy | | NATIVE |
| server_servlet_context-path | | /spring-oidc |
| keycloak_realm | | |
| keycloak_public-client | | |
| keycloak_auth-server-url | | |
| keycloak_ssl-required | | |
| keycloak_resource | | |
| keycloak_use-resource-role-mappings | | |
| keycloak_truststore | | |
| keycloak_truststore-password | | |
| keycloak_principal-attribute | | |
| CROWD_APPLICATION_NAME | | spring_crowd_sso |
| CROWD_APPLICATION_PASSWORD | | spring_crowd_sso |
| CROWD_SERVER_URL | | http://localhost:8095/crowd/services/ |
| CROWD_BASE_URL | | http://localhost:8095/crowd/ |
| CROWD_APPLICATION_LOGIN_URL | | http://localhost:8095/crowd/console/ |
| CROWD_COOKIE_TOKEN_KEY | | crowd.token_key |
| CROWD_SESSION_IS_AUTHENTICATED | | session.isauthenticated |
| CROWD_SESSION_TOKEN_KEY | | session.tokenkey |
| CROWD_SESSION_VALIDATION_INTERVAL | | 2 |
| CROWD_SESSION_LAST_VALIDATION | | session.lastvalidation |
<br/>

## Notes
You may have to add ssl certs for keycloak domain.

If you hit https://demo.cloudbrocktec.com/spring-oidc/token without a redirect parameter then it will take you to a page that shows your OpenId username.
