package com.gzdzsss.authserver.config.jwt;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author <a href="mailto:zhouyanjie666666@gmail.com">zyj</a>
 * @date 2019/4/19
 */


public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

    private MacSigner macSigner;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, MacSigner macSigner) {
        super(authenticationManager);
        this.macSigner = macSigner;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(JwtUtils.decodeToken(request, macSigner));
        chain.doFilter(request, response);
    }
}
