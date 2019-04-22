package com.gzdzsss.authserver.config;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author <a href="mailto:zhouyanjie666666@gmail">zyj</a>
 * @date 2019/4/2
 */

public class JwtAuthentication extends AbstractAuthenticationToken {

    private final Object principal;

    public JwtAuthentication(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
