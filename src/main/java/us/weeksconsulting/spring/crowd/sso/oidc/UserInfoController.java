package us.weeksconsulting.spring.crowd.sso.oidc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class UserInfoController {

    private static final Logger LOG = LogManager.getLogger();

    @GetMapping(path = "/userInfo")
    public String userInfo(final Principal principal, final Model model) {
        LOG.traceEntry();

        model.addAttribute("username", principal.getName());

        final IDToken idToken = extractIdToken(principal);
        final AccessToken accessToken = extractAccessToken(principal);
        model.addAttribute("idToken", idToken);
        model.addAttribute("accessToken", accessToken);

        LOG.traceExit();
        return "user";
    }

    public AccessToken extractAccessToken(final Principal principal) {
        LOG.traceEntry();

        final KeycloakAuthenticationToken keycloakAuthToken = (KeycloakAuthenticationToken) principal;
        final KeycloakPrincipal keycloakPrincipal = (KeycloakPrincipal) keycloakAuthToken.getPrincipal();

        final AccessToken token = keycloakPrincipal.getKeycloakSecurityContext().getToken();

        LOG.traceExit();
        return token;
    }

    public IDToken extractIdToken(final Principal principal) {
        LOG.traceEntry();

        final KeycloakAuthenticationToken keycloakAuthToken = (KeycloakAuthenticationToken) principal;
        final KeycloakPrincipal keycloakPrincipal = (KeycloakPrincipal) keycloakAuthToken.getPrincipal();

        final IDToken token = keycloakPrincipal.getKeycloakSecurityContext()
                                               .getIdToken();
        LOG.traceExit();
        return token;
    }
}
