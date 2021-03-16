//package us.weeksconsulting.spring.crowd.sso.configuration;
//
//import com.atlassian.crowd.embedded.api.PasswordCredential;
//import com.atlassian.crowd.integration.http.CrowdHttpAuthenticator;
//import com.atlassian.crowd.integration.springsecurity.CrowdSSOAuthenticationDetails;
//import com.atlassian.crowd.integration.springsecurity.RemoteCrowdAuthenticationProvider;
//import com.atlassian.crowd.integration.springsecurity.user.CrowdUserDetailsService;
//import com.atlassian.crowd.model.authentication.UserAuthenticationContext;
//import com.atlassian.crowd.model.authentication.ValidationFactor;
//import com.atlassian.crowd.service.client.ClientProperties;
//import com.atlassian.crowd.service.client.CrowdClient;
//import java.util.List;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.env.Environment;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import us.weeksconsulting.spring.crowd.sso.properties.PropertyKeys;
//
//public class CrowdAuthenticationProviderImpl extends RemoteCrowdAuthenticationProvider {
//
//  private static final Logger LOG = LogManager.getLogger();
//
//  @Autowired
//  private Environment env;
//
//  @Autowired
//  private ClientProperties crowdProperties;
//
//  public CrowdAuthenticationProviderImpl(final CrowdClient authenticationManager,
//      final CrowdHttpAuthenticator httpAuthenticator,
//      final CrowdUserDetailsService userDetailsService) {
//    super(authenticationManager, httpAuthenticator, userDetailsService);
//  }
//
//  @Override
//  public Authentication authenticate(final Authentication auth) throws AuthenticationException {
//    LOG.traceEntry();
//
//    try {
//      final UserAuthenticationContext uc = new UserAuthenticationContext();
//      uc.setApplication(crowdProperties.getApplicationName());
//      uc.setName(auth.getName());
//      uc.setCredential(new PasswordCredential("NONE"));
//      /*Jira seems to be hard-coded to look for this validation.
//                 So we will have to use it as well.*/
//      final String remoteAddress = env.getProperty(PropertyKeys.CROWD_VALIDATION_REMOTE_ADDRESS);
//
//      if (null == remoteAddress || remoteAddress.trim().isEmpty()) {
//        final CrowdSSOAuthenticationDetails details = ((CrowdCookieAuthenticator) auth).getCrowdDetails();
//        final List<ValidationFactor> defaultValidationFactors = details.getValidationFactors();
//        uc.setValidationFactors(defaultValidationFactors.toArray(new ValidationFactor[0]));
//      } else {
//        final ValidationFactor remoteAddrValidationFactor = new ValidationFactor("remote_address", remoteAddress);
//        uc.setValidationFactors(new ValidationFactor[]{remoteAddrValidationFactor});
//      }
//      LOG.debug("userContext: {}", uc);
//      final String token = authenticationManager.authenticateSSOUserWithoutValidatingPassword(uc);
//
//      final CrowdCookieAuthenticator authObj = new CrowdCookieAuthenticator(auth.getPrincipal(), token, ((CrowdCookieAuthenticator) auth).getCrowdDetails(), auth.getAuthorities(), auth.getCredentials());
//
//      authObj.setAuthenticated(true);
//      LOG.traceExit();
//      return authObj;
//    } catch (Exception e) {
//      LOG.error(e);
//      throw new AtlassianAuthException("Failed to authenticate with crowd.", e);
//    }
//  }
//
//  public static class AtlassianAuthException extends AuthenticationException {
//
//    public AtlassianAuthException(final String msg, final Throwable t) {
//      super(msg, t);
//    }
//
//    public AtlassianAuthException(final String msg) {
//      super(msg);
//    }
//
//  }
//
//  @Override
//  public boolean supports(final Class<? extends Object> aClass) {
//    return CrowdCookieAuthenticator.class.isAssignableFrom(aClass);
//  }
//
//}
