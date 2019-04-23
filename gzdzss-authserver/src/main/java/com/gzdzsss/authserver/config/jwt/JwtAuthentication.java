package com.gzdzsss.authserver.config.jwt;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author <a href="mailto:zhouyanjie666666@gmail">zyj</a>
 * @date 2019/4/2
 */

public class JwtAuthentication extends AbstractAuthenticationToken {

    private final Object principal;

    @Getter
    private final String accessToken;

    @Getter
    private final String clientId;

    public JwtAuthentication(String accessToken, String clientId, Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.accessToken = accessToken;
        this.principal = principal;
        this.clientId = clientId;
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
