package org.cbioportal.security.spring.authentication.oidc;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;

/**
 * Configuration class for oidc.
 *
 * @author Manda Wilson
 */
class PortalGrantedAuthoritiesMapper implements GrantedAuthoritiesMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(PortalGrantedAuthoritiesMapper.class);

    // we are going to ignore the authorities we are given (we won't be given any) 
    public Set<GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
        LOGGER.debug("Mapping authorities...");
        Set<GrantedAuthority> toReturnAuthorities = new HashSet<>();
        toReturnAuthorities.add(new SimpleGrantedAuthority("ROLE_ALL"));
        toReturnAuthorities.add(new SimpleGrantedAuthority("ROLE_BLAH"));
        LOGGER.debug("Done mapping authorities...");
        return toReturnAuthorities;
    }

}
