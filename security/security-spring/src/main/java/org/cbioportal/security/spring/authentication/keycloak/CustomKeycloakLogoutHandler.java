/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cbioportal.security.spring.authentication.keycloak;

import java.util.*;
import org.springframework.util.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.keycloak.adapters.AdapterDeploymentContext;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.adapters.spi.HttpFacade;
import org.keycloak.adapters.springsecurity.facade.SimpleHttpFacade;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
/**
 *
 * @author ochoaa
 */
public class CustomKeycloakLogoutHandler implements LogoutHandler {

    /**
     * @return the logoutUrl
     */
    public String getLogoutUrl() {
        return logoutUrl;
    }

    /**
     * @param logoutUrl the logoutUrl to set
     */
    public void setLogoutUrl(String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }

    /**
     * @return the adapterDeploymentContext
     */
    public AdapterDeploymentContext getAdapterDeploymentContext() {
        return adapterDeploymentContext;
    }

    /**
     * @param adapterDeploymentContext the adapterDeploymentContext to set
     */
    public void setAdapterDeploymentContext(AdapterDeploymentContext adapterDeploymentContext) {
        this.adapterDeploymentContext = adapterDeploymentContext;
    }

    private static final Log log = LogFactory.getLog(CustomKeycloakLogoutHandler.class);
    private String logoutUrl = "/saml/logout";
    private AdapterDeploymentContext adapterDeploymentContext;
    
    public CustomKeycloakLogoutHandler(AdapterDeploymentContext adapterDeploymentContext) {
        Assert.notNull(adapterDeploymentContext);
        this.adapterDeploymentContext = adapterDeploymentContext;
        this.logoutUrl = "/saml/logout";
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        Object details = authentication.getDetails();
        if (details.getClass().isAssignableFrom(OAuth2AuthenticationDetails.class)) {

            String accessToken = ((OAuth2AuthenticationDetails)details).getTokenValue();
            log.debug("token: {}" +  accessToken);

            RestTemplate restTemplate = new RestTemplate();

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("token", accessToken);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "bearer " + accessToken);

            HttpEntity<String> request2 = new HttpEntity(params, headers);

            HttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
            HttpMessageConverter stringHttpMessageConverternew = new StringHttpMessageConverter();
            restTemplate.setMessageConverters(Arrays.asList(new HttpMessageConverter[]{formHttpMessageConverter, stringHttpMessageConverternew}));
            try {
                ResponseEntity<String> response2 = restTemplate.exchange(getLogoutUrl(), HttpMethod.POST, request2, String.class);
            } catch(HttpClientErrorException e) {
                log.error("HttpClientErrorException invalidating token with SSO authorization server. response.status code: {}, server URL: {}", e);
            }
        }
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

//    protected void handleSingleSignOut(HttpServletRequest request, HttpServletResponse response, KeycloakAuthenticationToken authenticationToken) {
//        HttpFacade facade = new SimpleHttpFacade(request, response);
//        KeycloakDeployment deployment = adapterDeploymentContext.resolveDeployment(facade);
//        RefreshableKeycloakSecurityContext session = (RefreshableKeycloakSecurityContext) authenticationToken.getAccount().getKeycloakSecurityContext();
//        session.logout(deployment);
//    }
}
