package us.weeksconsulting.spring.crowd.sso.configuration;

import com.atlassian.crowd.integration.springsecurity.CrowdSSOAuthenticationDetails;
import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 *
 * @author Chris
 */
public class CrowdCookieAuthenticator extends AbstractAuthenticationToken {

  private final Object principal;
  private final String crowdToken;
  private final Object credentials;
  private final CrowdSSOAuthenticationDetails crowdDetails;

  public CrowdCookieAuthenticator(final Object principal,
      final String crowdToken, final CrowdSSOAuthenticationDetails crowdDetails,
      final Collection<? extends GrantedAuthority> authorities,
      final Object credentials) {
    super(authorities);
    this.credentials = credentials;
    this.principal = principal;
    this.crowdToken = crowdToken;
    this.crowdDetails = crowdDetails;
  }

  public CrowdCookieAuthenticator(final Object principal,
      final String crowdToken,
      final Collection<? extends GrantedAuthority> authorities,
      final Object credentials) {
    super(authorities);
    this.credentials = credentials;
    this.principal = principal;
    this.crowdToken = crowdToken;
    this.crowdDetails = null;
  }

  public CrowdSSOAuthenticationDetails getCrowdDetails() {
    return crowdDetails;
  }

  public String getCrowdToken() {
    return crowdToken;
  }

  @Override
  public Object getCredentials() {
    return credentials;
  }

  @Override
  public Object getPrincipal() {
    return principal;
  }
}
