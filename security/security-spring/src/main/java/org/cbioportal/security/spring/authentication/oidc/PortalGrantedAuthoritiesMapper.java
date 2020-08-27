package org.cbioportal.security.spring.authentication.oidc;

import java.util.*;

import org.cbioportal.model.User;
import org.cbioportal.model.UserAuthorities;
import org.cbioportal.persistence.SecurityRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.stereotype.Component;

/**
 * Configuration class for oidc.
 *
 * @author Manda Wilson
 */
@Component
class PortalGrantedAuthoritiesMapper implements GrantedAuthoritiesMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(PortalGrantedAuthoritiesMapper.class);

    @Autowired
    private SecurityRepository securityRepository;

    // we are going to ignore the authorities we are given (we won't be given any) 
    public Set<GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
        LOGGER.debug("Mapping authorities...");
        String email = null;
        for (GrantedAuthority authority : authorities) {
            if (OidcUserAuthority.class.isInstance(authority)) {
                OidcUserAuthority oidcUserAuthority = (OidcUserAuthority) authority;

                OidcIdToken idToken = oidcUserAuthority.getIdToken();
                OidcUserInfo userInfo = oidcUserAuthority.getUserInfo();

                // TODO delete most/all of the logging
                LOGGER.debug("idToken: " + idToken);
                LOGGER.debug("userInfo: " + userInfo);

                if (userInfo != null) {
                    LOGGER.debug("userInfo.getEmail(): " + userInfo.getEmail());
                    LOGGER.debug("userInfo.getFullName(): " + userInfo.getFullName());
                    LOGGER.debug("userInfo.getPreferredUsername(): " + userInfo.getPreferredUsername());
                    LOGGER.debug("userInfo.getSubject(): " + userInfo.getSubject());

                    email = userInfo.getEmail();
                } else if (idToken != null) {
                    LOGGER.debug("idToken.getTokenValue(): " + idToken.getTokenValue()); 
                    LOGGER.debug("idToken.getEmail(): " + idToken.getEmail()); 
                    if (idToken.getEmail() != null) {
                        email = idToken.getEmail();
                    }
                    LOGGER.debug("idToken.getAccessTokenHash(): " + idToken.getAccessTokenHash()); 
                    // userInfo is null from Synapse
                    Map<String, Object> tokenClaims = idToken.getClaims();
                    LOGGER.debug("Printing tokenClaims");
                    for (Map.Entry<String, Object> entry : tokenClaims.entrySet()) {
                        LOGGER.debug("Key : " + entry.getKey() + " Value : " + entry.getValue());
                    }
                }
            } else if (OAuth2UserAuthority.class.isInstance(authority)) {
                // TODO this one is not being called, maybe delete or throw error?  or leave?
                // delete because we aren't actually getting the email
                OAuth2UserAuthority oauth2UserAuthority = (OAuth2UserAuthority) authority;

                Map<String, Object> userAttributes = oauth2UserAuthority.getAttributes();
                LOGGER.debug("Printing userAttributes");
                for (Map.Entry<String, Object> entry : userAttributes.entrySet()) {
                    LOGGER.debug("Key : " + entry.getKey() + " Value : " + entry.getValue());
                }
            }
        }

        Set<GrantedAuthority> toReturnAuthorities = new HashSet<>();
        if (email == null) {
    		throw new UsernameNotFoundException("Error: not given a email from IDP");
        } else {
            //try to find user in DB
            User user = securityRepository.getPortalUser(email);
        	if (user != null && user.isEnabled()) {
                LOGGER.debug("mapAuthorities(), user is enabled; attempting to fetch portal user authorities, userid: " + email);
    
                UserAuthorities portalAuthorities = securityRepository.getPortalUserAuthorities(email);
                if (portalAuthorities != null) {
                    toReturnAuthorities =
                        new HashSet<>(AuthorityUtils.createAuthorityList(portalAuthorities.getAuthorities().toArray(new String[portalAuthorities.getAuthorities().size()])));
                } 
            } else if (user == null) { // new user
              LOGGER.debug("mapAuthorities(), user authorities is null, email: " + email + ". Depending on property always_show_study_group, "
                  + "she could still have default access (to PUBLIC studies)");
        	} else {
        		// user WAS found in DB but has been actively disabled:
        		throw new UsernameNotFoundException("Error: Your user access to cBioPortal has been disabled");
        	}
    
            // toReturnAuthorities.add(new SimpleGrantedAuthority("ALL"));
        }
        LOGGER.debug("Done mapping authorities...");
        return toReturnAuthorities;
    }

}
