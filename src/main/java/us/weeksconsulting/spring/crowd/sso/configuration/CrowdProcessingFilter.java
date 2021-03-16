//package us.weeksconsulting.spring.crowd.sso.configuration;
//
//import com.atlassian.crowd.integration.http.util.CrowdHttpTokenHelper;
//import com.atlassian.crowd.integration.springsecurity.CrowdSSOAuthenticationDetails;
//import com.atlassian.crowd.model.authentication.ValidationFactor;
//import com.atlassian.crowd.service.client.ClientProperties;
//import com.atlassian.crowd.service.client.CrowdClient;
//import java.io.IOException;
//import java.util.List;
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
//
///**
// *
// * @author Chris
// */
//public class CrowdProcessingFilter extends AbstractAuthenticationProcessingFilter {
//
//  private static final Logger LOG = LogManager.getLogger();
//
//  public static final String FILTER_URL = "/saml/sso";
//
//  private final ClientProperties clientProperties;
//  private final CrowdHttpTokenHelper tokenHelper;
//  private final CrowdClient crowdClient;
//
//  public CrowdProcessingFilter(
//      final CrowdHttpTokenHelper tokenHelper,
//      final CrowdClient crowdClient,
//      final ClientProperties clientProperties) {
//    super(FILTER_URL);
//    this.clientProperties = clientProperties;
//    this.tokenHelper = tokenHelper;
//    this.crowdClient = crowdClient;
//  }
//
//  @Override
//  public Authentication attemptAuthentication(final HttpServletRequest request,
//      final HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
//    LOG.traceEntry();
//
//    final CrowdSSOAuthenticationDetails details = this.getAuthenticationDetails(request);
//    final CrowdCookieAuthenticator crowdAuth = new CrowdCookieAuthenticator(request.getUserPrincipal(), null, details, null, null);
//
//    final Authentication crowdAuthResult = getAuthenticationManager().authenticate(crowdAuth);
//
//    LOG.traceExit();
//    return crowdAuthResult;
//  }
//
//  @Override
//  protected void successfulAuthentication(final HttpServletRequest request,
//      final HttpServletResponse response, final FilterChain chain,
//      final Authentication authResult) throws IOException, ServletException {
//    LOG.traceEntry();
//
//    this.storeTokenIfCrowd(request, response, authResult);
//    super.successfulAuthentication(request, response, chain, authResult);
//
//    LOG.traceExit();
//  }
//
//  protected void storeTokenIfCrowd(final HttpServletRequest request,
//      final HttpServletResponse response, final Authentication authResult) {
//    LOG.traceEntry();
//
//    if (authResult instanceof CrowdCookieAuthenticator && ((CrowdCookieAuthenticator) authResult).getCrowdToken() != null) {
//      final CrowdCookieAuthenticator crowdAuth = (CrowdCookieAuthenticator) authResult;
//      try {
//        this.tokenHelper.setCrowdToken(request, response, crowdAuth.getCrowdToken(), this.clientProperties, this.crowdClient.getCookieConfiguration());
//      } catch (Exception ex) {
//        LOG.error("Unable to set Crowd SSO token", ex);
//      }
//    }
//
//    LOG.traceExit();
//  }
//
//  protected CrowdSSOAuthenticationDetails getAuthenticationDetails(
//      final HttpServletRequest request) {
//    final List<ValidationFactor> validationFactors = this.tokenHelper.getValidationFactorExtractor().getValidationFactors(request);
//    final String application = this.clientProperties.getApplicationName();
//
//    return new CrowdSSOAuthenticationDetails(application, validationFactors);
//  }
//}
