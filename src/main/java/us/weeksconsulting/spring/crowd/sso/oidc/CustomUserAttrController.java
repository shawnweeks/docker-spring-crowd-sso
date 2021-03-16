package us.weeksconsulting.spring.crowd.sso.oidc;

import java.security.Principal;
import java.util.Map;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomUserAttrController {
    @GetMapping(path = "/users")
    public String getUserInfo(Model model) {

        KeycloakAuthenticationToken authentication = (KeycloakAuthenticationToken) SecurityContextHolder.getContext()
                .getAuthentication();

        final Principal principal = (Principal) authentication.getPrincipal();

        String dob = "";

        if (principal instanceof KeycloakPrincipal) {

            KeycloakPrincipal<KeycloakSecurityContext> kPrincipal = (KeycloakPrincipal<KeycloakSecurityContext>) principal;
            IDToken token = kPrincipal.getKeycloakSecurityContext()
                    .getIdToken();

            AccessToken accessToken = kPrincipal.getKeycloakSecurityContext().getToken();
            Map<String, Object> otherClaims = accessToken.getOtherClaims();
            System.out.println("accessToken: " + otherClaims);
            System.out.println("email: " + token.getEmail());
            System.out.println("otherClaims length: " + otherClaims.size());

            Map<String, Object> customClaims = token.getOtherClaims();

            System.out.println("customClaims length: " + customClaims.size());
            System.out.println(customClaims);

            if (customClaims.containsKey("DOB")) {
                dob = String.valueOf(customClaims.get("DOB"));
            }
        }

        model.addAttribute("username", principal.getName());
        model.addAttribute("dob", dob);
        return "userInfo";
    }

}
