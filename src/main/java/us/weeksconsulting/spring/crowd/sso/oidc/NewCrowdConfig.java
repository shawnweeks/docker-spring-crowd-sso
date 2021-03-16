package us.weeksconsulting.spring.crowd.sso.oidc;

import com.atlassian.crowd.integration.http.util.CrowdHttpTokenHelper;
import com.atlassian.crowd.integration.http.util.CrowdHttpTokenHelperImpl;
import com.atlassian.crowd.integration.http.util.CrowdHttpValidationFactorExtractor;
import com.atlassian.crowd.integration.http.util.CrowdHttpValidationFactorExtractorImpl;
import com.atlassian.crowd.integration.rest.service.factory.RestCrowdClientFactory;
import com.atlassian.crowd.service.client.ClientProperties;
import com.atlassian.crowd.service.client.ClientPropertiesImpl;
import com.atlassian.crowd.service.client.CrowdClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.env.Environment;
import us.weeksconsulting.spring.crowd.sso.properties.CrowdPropertyKeys;

import java.util.Properties;

@Configuration
public class NewCrowdConfig {
    private static final Logger LOG = LogManager.getLogger();

    @Autowired
    private Environment env;

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
    public CrowdHttpTokenHelper tokenHelper() {
        return CrowdHttpTokenHelperImpl.getInstance(validationFactorExtractor());
    }

    @Bean
    public CrowdHttpValidationFactorExtractor validationFactorExtractor() {
        return CrowdHttpValidationFactorExtractorImpl.getInstance();
    }
}
