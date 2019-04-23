package com.gzdzsss.authserver.config.jwt;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.jwt.crypto.sign.SignerVerifier;
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


    private SignerVerifier signerVerifier;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, SignerVerifier signerVerifier) {
        super(authenticationManager);
        this.signerVerifier = signerVerifier;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(JwtUtils.decodeToken(request, signerVerifier));
        chain.doFilter(request, response);
    }
}
