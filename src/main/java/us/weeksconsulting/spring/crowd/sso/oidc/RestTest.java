package us.weeksconsulting.spring.crowd.sso.oidc;

import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.integration.http.util.CrowdHttpTokenHelper;
import com.atlassian.crowd.integration.springsecurity.CrowdSSOAuthenticationDetails;
import com.atlassian.crowd.model.authentication.UserAuthenticationContext;
import com.atlassian.crowd.model.authentication.ValidationFactor;
import com.atlassian.crowd.service.client.ClientProperties;
import com.atlassian.crowd.service.client.CrowdClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import us.weeksconsulting.spring.crowd.sso.properties.PropertyKeys;

@Controller
//@RestController
public class RestTest {

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
    public String getCrowdToken(HttpServletRequest request, HttpServletResponse response, Principal principal,
                                Model model) throws Exception {
        LOG.traceEntry();

        LOG.info("DERP");
        
        model.addAttribute("username", principal.getName());

        final UserAuthenticationContext uc = new UserAuthenticationContext();
        uc.setApplication(crowdProperties.getApplicationName());
        uc.setName(principal.getName());
        uc.setCredential(new PasswordCredential("NONE"));
        final CrowdSSOAuthenticationDetails details = this.getAuthenticationDetails(request);
        final List<ValidationFactor> defaultValidationFactors = details.getValidationFactors();
        uc.setValidationFactors(defaultValidationFactors.toArray(new ValidationFactor[0]));
        LOG.debug("userContext: {}", uc);

        final String token = crowdClient.authenticateSSOUserWithoutValidatingPassword(uc);

        this.storeTokenIfCrowd(request, response, token);

        String redirectTo = request.getParameter("redirectTo");

        if (null == redirectTo || redirectTo.trim().isEmpty()) {
//            redirectTo = env.getRequiredProperty(PropertyKeys.DEFAULT_REDIRECT_URL);
//            request.getSession(true).invalidate();
            return "customers";
        }

//        response.sendRedirect(redirectTo);

        request.getSession(true).invalidate();

        LOG.traceExit();
        return "redirect:" + redirectTo;
    }

    CrowdSSOAuthenticationDetails getAuthenticationDetails(
            final HttpServletRequest request) {
        final List<ValidationFactor> validationFactors = this.tokenHelper.getValidationFactorExtractor().getValidationFactors(request);
        final String application = crowdProperties.getApplicationName();

        return new CrowdSSOAuthenticationDetails(application, validationFactors);
    }

    protected void storeTokenIfCrowd(final HttpServletRequest request, final HttpServletResponse response, final String crowdToken) {
        LOG.traceEntry();

        try {
            tokenHelper.setCrowdToken(request, response, crowdToken, crowdProperties, crowdClient.getCookieConfiguration());
        } catch (Exception ex) {
            LOG.error("Unable to set Crowd SSO token", ex);
        }

        LOG.traceExit();
    }
}
