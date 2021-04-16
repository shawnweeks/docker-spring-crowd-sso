# Dockerized Spring-boot Crowd SSO Login App
Provides a SAML based login point that can generate a Crowd SSO token.
Since Atlassian uses a cookie to track your SSO session, this application and the Atlassian tools must be presented to the user from the same domain. Examples "my-domain.com/jira" or "jira.my-domain.com".

This is a Docker image using Tomcat 9. The app itself only provides the Crowd SSO login functionality. The SAML authentication is performed by Tomcat using the Keycloak Tomcat SAML adapter valve.

## Build and Run

```shell
docker build \
    -t $REGISTRY/spring-crowd-sso:1.0.0 \
    --build-arg REGISTRY=$REGISTRY \
    .
    
docker push $REGISTRY/spring-crowd-sso
```

### Simple SSL Run
```shell
keytool -genkey -noprompt -keyalg RSA \
        -alias selfsigned -keystore keystore.jks -storetype jks \
        -storepass changeit -keypass changeit \
        -dname "CN=localhost" \
        -validity 360 -keysize 2048

docker run -it --rm --init --name='spring-crowd-sso' \
    -p 8443:8443 \
    -v $PWD/keystore.jks:/tmp/keystore.jks \
    -e TOMCAT_PORT=8443 \
    -e TOMCAT_SCHEME=https \
    -e TOMCAT_SECURE=true \
    -e TOMCAT_SSL_ENABLED=true \
    -e TOMCAT_KEY_ALIAS=selfsigned \
    -e TOMCAT_KEYSTORE_FILE=/tmp/keystore.jks \
    -e TOMCAT_KEYSTORE_PASSWORD=changeit \
    -e CROWD_SERVER_URL='http://crowd:8095/crowd/services/' \
    -e CROWD_BASE_URL='http://crowd:8095/crowd/' \
    -e CROWD_APPLICATION_LOGIN_URL='http://crowd:8095/crowd/console/' \
    -e TOMCAT_SAML_ENABLED=true \
    -e TOMCAT_SAML_SP_ENTITY_ID=tomcat \
    -e TOMCAT_SAML_SP_SIGN_KEY=true \
    -e TOMCAT_SAML_SP_KEY='PEM_KEY_HERE' \
    -e TOMCAT_SAML_SP_CERT='PEM_CERT_HERE' \
    -e TOMCAT_SAML_IDP_ENTITY_ID=idp \
    -e TOMCAT_SAML_IDP_SIGN_REQ=true \
    -e TOMCAT_SAML_IDP_BIND_URL='https://auth.your-domain.com/realms/master/protocol/saml' \
    -e TOMCAT_SAML_IDP_SSO_BIND_URL='https://auth.your-domain.com/realms/master/protocol/saml' \
    -e TOMCAT_SAML_IDP_META_URL='https://auth.your-domain.com/realms/master/protocol/saml/descriptor' \
    $REGISTRY/spring-crowd-sso:1.0.0
```

###  Example Run Command
```shell
docker run -it --rm --init --name='spring-crowd-sso' \
    -p 8080:8080 \
    -e CROWD_SERVER_URL='http://crowd:8095/crowd/services/' \
    -e CROWD_BASE_URL='http://crowd:8095/crowd/' \
    -e CROWD_APPLICATION_LOGIN_URL='http://crowd:8095/crowd/console/' \
    -e TOMCAT_SAML_ENABLED=true \
    -e TOMCAT_SAML_SP_ENTITY_ID=tomcat \
    -e TOMCAT_SAML_SP_SIGN_KEY=true \
    -e TOMCAT_SAML_SP_KEY='PEM_KEY_HERE' \
    -e TOMCAT_SAML_SP_CERT='PEM_CERT_HERE' \
    -e TOMCAT_SAML_IDP_ENTITY_ID=idp \
    -e TOMCAT_SAML_IDP_SIGN_REQ=true \
    -e TOMCAT_SAML_IDP_BIND_URL='https://auth.your-domain.com/realms/master/protocol/saml' \
    -e TOMCAT_SAML_IDP_SSO_BIND_URL='https://auth.your-domain.com/realms/master/protocol/saml' \
    -e TOMCAT_SAML_IDP_META_URL='https://auth.your-domain.com/realms/master/protocol/saml/descriptor' \
    $REGISTRY/spring-crowd-sso:1.0.0
```

### Run Parameters
| Environment Variable | Description | Default|
| --- | --- | ---|
| TOMCAT_PORT | Port Tomcat listens on. | 8080 |
| TOMCAT_PROXY_NAME | External URL for Reverse Proxy | |
| TOMCAT_PROXY_PORT | External Port for Reverse Proxy | 443|
| TOMCAT_SCHEME | URL Schema | https |
| TOMCAT_SECURE | URL Secure | true |
| TOMCAT_SAML_ENABLED | Enables Keycloak SAML Adapter for Tomcat | false|
| TOMCAT_SAML_SP_ENTITY_ID | The identifier for this client | |
| TOMCAT_SAML_SP_LOGOUT_PAGE | See Keycloak Documentation | None |
| TOMCAT_SAML_SP_SSL_POLICY | See Keycloak Documentation | None |
| TOMCAT_SAML_SP_NAME_ID_POLICY_FORMAT | See Keycloak Documentation | None |
| TOMCAT_SAML_SP_FORCE_AUTH | See Keycloak Documentation | None |
| TOMCAT_SAML_SP_IS_PASSIVE | See Keycloak Documentation | None |
| TOMCAT_SAML_SP_SIGN_KEY | Use key to sign requests  | true|
| TOMCAT_SAML_SP_ENCR_KEY | Use key to encrypt requests  | false|
| TOMCAT_SAML_SP_KEY | SP private key in PEM format | |
| TOMCAT_SAML_SP_CERT | SP certificate in PEM format | |
| TOMCAT_SAML_SP_NAME_MAP_POLICY | Name mapping policy to be used, can be used to map name from email or other attributes | FROM_NAME_ID |
| TOMCAT_SAML_SP_NAME_MAP_ATTR | If Name mapping policy is set then use this attribute for mapping | None |
| TOMCAT_SAML_IDP_ENTITY_ID | This is the issuer ID of the IDP. For Keycloak this is 'idp' but that may vary. This setting is REQUIRED. | |
| TOMCAT_SAML_IDP_SIGN_REQ | Does the IDP Require Signatures | true|
| TOMCAT_SAML_IDP_REQ_BIND | Request binding method | POST |
| TOMCAT_SAML_IDP_REP_BIND| Response binding method | POST |
| TOMCAT_SAML_IDP_SSO_BIND_URL | This is the URL for the IDP login service that the client will send requests to. This setting is REQUIRED. | None |
| TOMCAT_SAML_IDP_SLS_BIND_URL | This is the URL for the IDP’s logout service when using the REDIRECT binding. This setting is REQUIRED. | None |
| DEFAULT_REDIRECT_URL | The default redirect URL used for succesful logins if no redirect parameter is included in the login request. | /landing |
| CROWD_APPLICATION_NAME | | spring_crowd_sso |
| CROWD_APPLICATION_PASSWORD | | |
| CROWD_SERVER_URL | | http://localhost:8095/crowd/services/ |
| CROWD_BASE_URL | | http://localhost:8095/crowd/ |
| CROWD_APPLICATION_LOGIN_URL | | http://localhost:8095/crowd/console/ |
| CROWD_COOKIE_TOKEN_KEY | | crowd.token_key |
| CROWD_SESSION_IS_AUTHENTICATED | | session.isauthenticated |
| CROWD_SESSION_TOKEN_KEY | | session.tokenkey |
| CROWD_SESSION_VALIDATION_INTERVAL | | 2 |
| CROWD_SESSION_LAST_VALIDATION | | session.lastvalidation |
<br/>

## Integrating With Crowd
1. Log-in to Crowd.
2. Click "Applications" in the top bar.
3. Click "Add application" in the side bar on the left.
4. For "Application type", select "Generic Application" from the dropdown menu.
5. For "Name", type in an appropriate name. This must be the same as the name given to the `CROWD_APPLICATION_NAME` property.
6. For "Password", type in an appropriate password and be sure to give the same password to the `CROWD_APPLICATION_PASSWORD` property.
7. Click "Next" on the bottom of the page.
8. Type in the URL to your application and click "Resolve IP Address". This probably won't work. I'm not sure what Crowd is trying to do here but this process seems hit-or-miss. It uses this to set the allowed remote addresses that Crowd will expect when your application makes requests to Crowd. My experience so far when this works is that Crowd will use the IP to whitelist the application. You can change this later to use the hostname instead.
9. If you get "failed to lookup ip address" then just enter `http://localhost` and click "Resolve IP Address" You should see `127.0.0.1` in the "Remote IP Address" field. We will manually change it later.
10. Click "Next".
11. Choose "Dev Crowd server" and click "Next".
12. Choose "Allow all users to authenticate" and click "Next".
13. Click "Add application".
14. Click the "Remote addresses" tab at the top.
15. Remove any addresses in the list.
16. Add the correct hostname or IP for your application.

## Notes
The login context path is `/spring-crowd-sso/saml/login`.

The redirect get query parameter is `redirectTo`. Example `https://login.my-domain.com/saml/login?redirectTo=https%3A%2F%2Fjira.my-domain.com`

The allowed roles can be set in the web.xml. It's currently set to allow any roles from the IDP. Keycloak recognizes `<role-name>**</role-name>` as a wildcard for any role.
