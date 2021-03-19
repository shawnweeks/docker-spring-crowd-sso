package us.weeksconsulting.spring.crowd.sso.oidc;

import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.integration.http.util.CrowdHttpTokenHelper;
import com.atlassian.crowd.integration.springsecurity.CrowdSSOAuthenticationDetails;
import com.atlassian.crowd.model.authentication.UserAuthenticationContext;
import com.atlassian.crowd.model.authentication.ValidationFactor;
import com.atlassian.crowd.service.client.ClientProperties;
import com.atlassian.crowd.service.client.CrowdClient;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.List;
import java.util.Map;

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

    public AccessToken extractAccessToken(Principal principal) {
        if (principal instanceof KeycloakAuthenticationToken) {
            KeycloakAuthenticationToken p = (KeycloakAuthenticationToken) principal;
            KeycloakPrincipal kp = (KeycloakPrincipal) p.getPrincipal();

            AccessToken token = kp.getKeycloakSecurityContext().getToken();

            return token;
        }

        return null;
    }

    public IDToken extractIdToken(Principal principal) {
        if (principal instanceof KeycloakAuthenticationToken) {
            KeycloakAuthenticationToken p = (KeycloakAuthenticationToken) principal;
            KeycloakPrincipal kp = (KeycloakPrincipal) p.getPrincipal();

            IDToken token = kp.getKeycloakSecurityContext()
                    .getIdToken();

            return token;
        }

        return null;
    }

    public void testClaims(Principal principal) {
        System.out.println("Checking principal from Spring.");
        if (principal instanceof KeycloakAuthenticationToken) {
            KeycloakAuthenticationToken p = (KeycloakAuthenticationToken) principal;
//            System.out.println(p.getPrincipal().getClass().getName());
            KeycloakPrincipal kp = (KeycloakPrincipal) p.getPrincipal();

            IDToken token = kp.getKeycloakSecurityContext()
                    .getIdToken();

            System.out.println("Roles: " + kp.getKeycloakSecurityContext().getToken().getRealmAccess().getRoles());
            AccessToken accessToken = kp.getKeycloakSecurityContext().getToken();
            Map<String, Object> otherClaims = accessToken.getOtherClaims();
            System.out.println("AccessToken otherClaims: " + otherClaims);
            System.out.println("AccessToken otherClaims length: " + otherClaims.size());

            Map<String, Object> customClaims = token.getOtherClaims();

            System.out.println("ID token otherClaims: " + customClaims);
            System.out.println("ID token otherClaims length: " + customClaims.size());
        }

        System.out.println("Checking principal from Keycloak auth context.");
        KeycloakAuthenticationToken authentication = (KeycloakAuthenticationToken) SecurityContextHolder.getContext()
                .getAuthentication();

        final Principal authPrincipal = (Principal) authentication.getPrincipal();
        if (authPrincipal instanceof KeycloakPrincipal) {
            KeycloakPrincipal<KeycloakSecurityContext> kPrincipal = (KeycloakPrincipal<KeycloakSecurityContext>) authPrincipal;
            IDToken token = kPrincipal.getKeycloakSecurityContext()
                    .getIdToken();

            AccessToken accessToken = kPrincipal.getKeycloakSecurityContext().getToken();
            Map<String, Object> otherClaims = accessToken.getOtherClaims();
            System.out.println("AccessToken: " + otherClaims);
            System.out.println("AccessToken otherClaims length: " + otherClaims.size());

            Map<String, Object> customClaims = token.getOtherClaims();

            System.out.println("ID token otherClaims: " + customClaims);
            System.out.println("ID token otherClaims length: " + customClaims.size());
        }
    }

    @GetMapping(path = "/token")
    public String getCrowdToken(HttpServletRequest request, HttpServletResponse response, Principal principal,
                                Model model) throws Exception {
        LOG.traceEntry();

        testClaims(principal);

//        System.out.println(principal.getClass().getName());

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
            IDToken idToken = extractIdToken(principal);
            AccessToken accessToken = extractAccessToken(principal);
            model.addAttribute("idToken", idToken);
            model.addAttribute("accessToken", accessToken);

            return "user";
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
