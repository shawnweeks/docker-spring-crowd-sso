package us.weeksconsulting.spring.crowd.sso.oidc;

import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.integration.http.util.CrowdHttpTokenHelper;
import com.atlassian.crowd.integration.springsecurity.CrowdSSOAuthenticationDetails;
import com.atlassian.crowd.model.authentication.UserAuthenticationContext;
import com.atlassian.crowd.model.authentication.ValidationFactor;
import com.atlassian.crowd.service.client.ClientProperties;
import com.atlassian.crowd.service.client.CrowdClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.List;

@Controller
public class CrowdTokenController {

    private static final Logger LOG = LogManager.getLogger();

    @Autowired
    private CrowdClient crowdClient;
    @Autowired
    private ClientProperties crowdProperties;
    @Autowired
    private CrowdHttpTokenHelper tokenHelper;
    @Autowired
    private Environment env;

    @GetMapping(path = "/token")
    public String getCrowdToken(final HttpServletRequest request, final HttpServletResponse response,
            final Principal principal) throws Exception {
        LOG.traceEntry();

        try {
            final UserAuthenticationContext uc = new UserAuthenticationContext();
            uc.setApplication(crowdProperties.getApplicationName());
            uc.setName(principal.getName());
            uc.setCredential(new PasswordCredential("NONE"));
            final CrowdSSOAuthenticationDetails details = this.getAuthenticationDetails(request);
            final List<ValidationFactor> defaultValidationFactors = details.getValidationFactors();
            uc.setValidationFactors(defaultValidationFactors.toArray(new ValidationFactor[0]));
            LOG.debug("userContext: {}", uc);

            final String token;

            token = crowdClient.authenticateSSOUserWithoutValidatingPassword(uc);

            this.storeTokenIfCrowd(request, response, token);

            final String redirectTo = request.getParameter("redirectTo");

            String view;

            if (null == redirectTo || redirectTo.trim().isEmpty()) {
                view = "forward:/userInfo";
            } else {
                view = "redirect:" + redirectTo;
            }

            LOG.traceExit();
            return view;
        } finally {
            request.getSession(true).invalidate();
        }
    }

    CrowdSSOAuthenticationDetails getAuthenticationDetails(final HttpServletRequest request) {
        LOG.traceEntry();

        final List<ValidationFactor> validationFactors =
                this.tokenHelper.getValidationFactorExtractor()
                                .getValidationFactors(request);

        final String application = crowdProperties.getApplicationName();

        final CrowdSSOAuthenticationDetails authDetails =
                new CrowdSSOAuthenticationDetails(application, validationFactors);

        LOG.traceExit();
        return authDetails;
    }

    protected void storeTokenIfCrowd(final HttpServletRequest request, final HttpServletResponse response,
            final String crowdToken) throws Exception {
        LOG.traceEntry();

        try {
            tokenHelper.setCrowdToken(request, response, crowdToken, crowdProperties,
                                      crowdClient.getCookieConfiguration());
        } catch (Exception ex) {
            LOG.error("Unable to set Crowd SSO token", ex);
            throw ex;
        }

        LOG.traceExit();
    }
}
