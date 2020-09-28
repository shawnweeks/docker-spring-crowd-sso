package us.weeksconsulting.spring.crowd.sso.configuration;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

/**
 *
 * @author Chris
 */
public class SavedReqHandler extends SimpleUrlAuthenticationSuccessHandler {

  private static final Logger LOG = LogManager.getLogger();

  @Override
  public void onAuthenticationSuccess(final HttpServletRequest request,
      final HttpServletResponse response, final Authentication authentication)
      throws ServletException, IOException {
    LOG.traceEntry();

    String redirectTo = request.getParameter("redirectTo");

    if (null == redirectTo || redirectTo.trim().isEmpty()) {
      redirectTo = this.getDefaultTargetUrl();
    }

    clearAuthenticationAttributes(request);
    LOG.debug("Redirecting to DefaultSavedRequest Url: {}", redirectTo);
    getRedirectStrategy().sendRedirect(request, response, redirectTo);

    request.getSession(true).invalidate();
    LOG.traceExit();
  }

}
