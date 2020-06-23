# Dockerized Spring-boot Crowd SSO Login App
Provides a SAML based login point that can generate a Crowd SSO token.
Since Atlassian uses a cookie to track your SSO session, this application and the Atlassian tools must be presented to the user from the same domain. Examples "my-domain.com/jira", "jira.my-domain.com".

This is a Docker image using Tomcat 9. The app itself only provides the Crowd SSO login functionality. The SAML authentication is performed by Tomcat using the Keycloak Tomcat SAML adapter valve.

## Build and Run

```sh
mvn clean package
docker build -t spring-crowd-sso .
docker run \
    -p 8080:8080 \
    spring-crowd-sso
```

### Environment Variables

If a property listed here has a value assigned, that value is the default.

+ `DEFAULT_REDIRECT_URL=/landing`: The default redirect URL used for succesful logins if no redirect parameter is included in the login request.
+ `TOMCAT_PROXY_NAME`: The proxy name for Tomcat to use.
+ `TOMCAT_PROXY_PORT`: The proxy port for Tomcat to use.
+ `TOMCAT_SCHEME=HTTP`: The HTTP scheme for Tomcat to use. (HTTP | HTTPS)
+ `TOMCAT_SECURE=false`: To set whether Tomcat requires a secure connection.
+ `TOMCAT_CONTEXTPATH`: The context path Tomcat will use for the login app.

#### Crowd Properties

+ `CROWD_APPLICATION_NAME=spring_crowd_sso`
+ `CROWD_APPLICATION_PASSWORD=spring_crowd_sso`
+ `CROWD_SERVER_URL=http://localhost:8095/crowd/services/`
+ `CROWD_BASE_URL=http://localhost:8095/crowd/`
+ `CROWD_APPLICATION_LOGIN_URL=http://localhost:8095/crowd/console/`
+ `CROWD_COOKIE_TOKEN_KEY=crowd.token_key`
+ `CROWD_SESSION_IS_AUTHENTICATED=session.isauthenticated`
+ `CROWD_SESSION_TOKEN_KEY=session.tokenkey`
+ `CROWD_SESSION_VALIDATION_INTERVAL=2`
+ `CROWD_SESSION_LAST_VALIDATION=session.lastvalidation`

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
The login context path is `/saml/login`.

The redirect get query parameter is `redirectTo`. Example `https://login.my-domain.com/saml/login?redirectTo=https%3A%2F%2Fjira.my-domain.com`

## Todo
+ I should set SAML keys up correctly for proper signature validation checks in the configs.