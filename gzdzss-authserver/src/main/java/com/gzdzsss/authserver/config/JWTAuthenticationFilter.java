package com.gzdzsss.authserver.config;

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


public class JWTAuthenticationFilter extends BasicAuthenticationFilter {


    private SignerVerifier signerVerifier;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, SignerVerifier signerVerifier) {
        super(authenticationManager);
        this.signerVerifier = signerVerifier;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader("authorization");
        if (header != null && header.toLowerCase().startsWith("bearer ")) {
            String token = header.substring(7);
            SecurityContextHolder.getContext().setAuthentication(JwtUtils.decodeToken(token, signerVerifier));
        }
        chain.doFilter(request, response);
    }
}
