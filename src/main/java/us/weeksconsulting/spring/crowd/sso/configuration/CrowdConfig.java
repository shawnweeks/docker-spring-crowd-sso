package us.weeksconsulting.spring.crowd.sso.configuration;

import com.atlassian.crowd.integration.http.CrowdHttpAuthenticatorImpl;
import com.atlassian.crowd.integration.http.util.CrowdHttpTokenHelper;
import com.atlassian.crowd.integration.http.util.CrowdHttpTokenHelperImpl;
import com.atlassian.crowd.integration.http.util.CrowdHttpValidationFactorExtractor;
import com.atlassian.crowd.integration.http.util.CrowdHttpValidationFactorExtractorImpl;
import com.atlassian.crowd.integration.rest.service.factory.RestCrowdClientFactory;
import com.atlassian.crowd.integration.springsecurity.RemoteCrowdAuthenticationProvider;
import com.atlassian.crowd.integration.springsecurity.user.CrowdUserDetailsServiceImpl;
import com.atlassian.crowd.service.client.ClientProperties;
import com.atlassian.crowd.service.client.ClientPropertiesImpl;
import com.atlassian.crowd.service.client.CrowdClient;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import us.weeksconsulting.spring.crowd.sso.properties.CrowdPropertyKeys;
import us.weeksconsulting.spring.crowd.sso.properties.PropertyKeys;

/**
 *
 * @author Chris
 */
@Configuration
public class CrowdConfig {

  private static final Logger LOG = LogManager.getLogger();

  @Autowired
  private Environment env;
  @Lazy
  @Autowired
  private AuthenticationManager authenticationManager;

  @Bean
  public SimpleUrlAuthenticationFailureHandler authenticationFailureHandler() {
    final SimpleUrlAuthenticationFailureHandler failureHandler
        = new SimpleUrlAuthenticationFailureHandler();
    failureHandler.setUseForward(true);
    failureHandler.setDefaultFailureUrl("/error");
    return failureHandler;
  }

  @Bean
  public SavedReqHandler successRedirectHandler() {
    final SavedReqHandler savedReq = new SavedReqHandler();
    final String defaultRedirectUrl = env.getRequiredProperty(PropertyKeys.DEFAULT_REDIRECT_URL);
    savedReq.setDefaultTargetUrl(defaultRedirectUrl);
    return savedReq;
  }

  @Bean
  public CrowdHttpTokenHelper tokenHelper() {
    return CrowdHttpTokenHelperImpl.getInstance(validationFactorExtractor());
  }

  @Bean
  public CrowdHttpValidationFactorExtractor validationFactorExtractor() {
    return CrowdHttpValidationFactorExtractorImpl.getInstance();
  }

  @Bean
  public CrowdClient crowdClient() {
    return crowdClientFactory().newInstance(clientProperties());
  }

  @Bean
  public RestCrowdClientFactory crowdClientFactory() {
    return new RestCrowdClientFactory();
  }

  @Bean
  ClientProperties clientProperties() {
    final Properties crowdProperties = new Properties();
    crowdProperties.setProperty("application.name", env.getProperty(CrowdPropertyKeys.APPLICATION_NAME));
    crowdProperties.setProperty("application.password", env.getProperty(CrowdPropertyKeys.APPLICATION_PASSWORD));
    crowdProperties.setProperty("application.login.url", env.getProperty(CrowdPropertyKeys.APPLICATION_LOGIN_URL));
    crowdProperties.setProperty("crowd.server.url", env.getProperty(CrowdPropertyKeys.CROWD_SERVER_URL));
    crowdProperties.setProperty("crowd.base.url", env.getProperty(CrowdPropertyKeys.CROWD_BASE_URL));
    crowdProperties.setProperty("session.isauthenticated", env.getProperty(CrowdPropertyKeys.SESSION_IS_AUTHENTICATED));
    crowdProperties.setProperty("session.tokenkey", env.getProperty(CrowdPropertyKeys.SESSION_TOKEN_KEY));
    crowdProperties.setProperty("session.validationinterval", env.getProperty(CrowdPropertyKeys.SESSION_VALIDATION_INTERVAL));
    crowdProperties.setProperty("session.lastvalidation", env.getProperty(CrowdPropertyKeys.SESSION_LAST_VALIDATION));
    crowdProperties.setProperty("cookie.tokenkey", env.getProperty(CrowdPropertyKeys.COOKIE_TOKEN_KEY));
    LOG.debug("crowdProperties: {}", crowdProperties);
    return ClientPropertiesImpl.newInstanceFromProperties(crowdProperties);
  }

  @Bean
  public CrowdUserDetailsServiceImpl crowdUserDetailsService() {
    final CrowdUserDetailsServiceImpl userDetailsService = new CrowdUserDetailsServiceImpl();
    userDetailsService.setCrowdClient(crowdClient());
    return userDetailsService;
  }

  @Bean
  public CrowdHttpAuthenticatorImpl crowdHttpAuthenticator() {
    return new CrowdHttpAuthenticatorImpl(crowdClient(), clientProperties(), tokenHelper());
  }

  @Bean
  public RemoteCrowdAuthenticationProvider crowdAuthenticationProvider() {
    return new CrowdAuthenticationProviderImpl(crowdClient(), crowdHttpAuthenticator(), crowdUserDetailsService());
  }

  @Bean
  public CrowdProcessingFilter crowdProcessingFilter() throws Exception {
    final CrowdProcessingFilter authFilter = new CrowdProcessingFilter(tokenHelper(), crowdClient(), clientProperties());
    authFilter.setFilterProcessesUrl("/saml/login");
    authFilter.setAuthenticationManager(authenticationManager);
    authFilter.setAuthenticationSuccessHandler(successRedirectHandler());
    authFilter.setAuthenticationFailureHandler(authenticationFailureHandler());

    return authFilter;
  }

  @Bean
  public FilterRegistrationBean registerNewProcessingFilter(
      final CrowdProcessingFilter filter) {
    final FilterRegistrationBean registration = new FilterRegistrationBean(filter);
    registration.setEnabled(false);
    return registration;
  }
}
