/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cbioportal.security.spring.authentication.keycloak;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;


import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.keycloak.adapters.AdapterDeploymentContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *  CustomKeycloakLogoutHandler currently exactly matches the LogoutHandler we already use for SAML authentication
 * org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler 
 * @author ochoaa
 */
public class CustomKeycloakLogoutHandler implements LogoutHandler {


    private boolean invalidateHttpSession = true;
    private boolean clearAuthentication = true;
    private static final Log log = LogFactory.getLog(CustomKeycloakLogoutHandler.class);
    private AdapterDeploymentContext adapterDeploymentContext;
    
    public CustomKeycloakLogoutHandler(AdapterDeploymentContext adapterDeploymentContext) {
        Assert.notNull(adapterDeploymentContext);
        this.adapterDeploymentContext = adapterDeploymentContext;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,   Authentication authentication) {
            String sessionId =  request.getSession().getId();
            log.info("\n\nSESSION ID = " + sessionId + "\n\n");

            Assert.notNull(request, "HttpServletRequest required");
            if (invalidateHttpSession) {
                    HttpSession session = request.getSession(false);
                    if (session != null) {
                            log.info("Invalidating session: " + session.getId());
                            session.invalidate();
                    }
            }

            if (clearAuthentication) {
                    SecurityContext context = SecurityContextHolder.getContext();
                    context.setAuthentication(null);
            }

            SecurityContextHolder.clearContext();
    }
    public AdapterDeploymentContext getAdapterDeploymentContext() {
        return adapterDeploymentContext;
    }
    
    public void setAdapterDeploymentContext(AdapterDeploymentContext adapterDeploymentContext) {
        this.adapterDeploymentContext = adapterDeploymentContext;
    }
    
    public boolean isInvalidateHttpSession() {
            return invalidateHttpSession;
    }

    public void setInvalidateHttpSession(boolean invalidateHttpSession) {
            this.invalidateHttpSession = invalidateHttpSession;
    }

    public void setClearAuthentication(boolean clearAuthentication) {
            this.clearAuthentication = clearAuthentication;
    }
//
//        if (details.getClass().isAssignableFrom(OAuth2AuthenticationDetails.class)) {
//
//            String accessToken = ((OAuth2AuthenticationDetails)details).getTokenValue();
//            log.info("token: {}" +  accessToken);
//
//            RestTemplate restTemplate = new RestTemplate();
//
//            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//            params.add("token", accessToken);
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.add("Authorization", "bearer " + accessToken);
//
//            HttpEntity<String> request2 = new HttpEntity(params, headers);
//
//            HttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
//            HttpMessageConverter stringHttpMessageConverternew = new StringHttpMessageConverter();
//            restTemplate.setMessageConverters(Arrays.asList(new HttpMessageConverter[]{formHttpMessageConverter, stringHttpMessageConverternew}));
//            try {
//                ResponseEntity<String> response2 = restTemplate.exchange(getLogoutUrl(), HttpMethod.POST, request2, String.class);
//            } catch(HttpClientErrorException e) {
//                log.error("HttpClientErrorException invalidating token with SSO authorization server. response.status code: {}, server URL: {}", e);
//            }
//        }
//        if (authentication == null) {
//            log.warn("Cannot log out without authentication");
//            return;
//        }
//        else if (!KeycloakAuthenticationToken.class.isAssignableFrom(authentication.getClass())) {
//            log.warn("Cannot log out a non-Keycloak authentication: {}", authentication);
//            return;
//        }
//
//        handleSingleSignOut(request, response, (KeycloakAuthenticationToken) authentication);

}