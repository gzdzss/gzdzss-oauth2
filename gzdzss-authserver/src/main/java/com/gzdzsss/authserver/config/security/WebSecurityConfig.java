package com.gzdzsss.authserver.config.security;

import com.alibaba.fastjson.JSONObject;
import com.gzdzss.security.util.GzdzssSecurityUtils;
import com.gzdzsss.authserver.config.jwt.JwtAuthenticationFilter;
import com.gzdzsss.authserver.config.jwt.JwtUtils;
import com.gzdzsss.authserver.config.oauth.RedisJwtTokenStore;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStoreSerializationStrategy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * @author <a href="mailto:zhouyanjie666666@gmail.com">zyj</a>
 * @date 2019/3/30
 */


@Slf4j
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MacSigner macSigner;

    @Autowired
    private UserDetailsService userDetailsService;

    @Value("${jwt.authExpiresInSeconds}")
    private int authExpiresInSeconds;

    @Autowired
    private DefaultTokenServices defaultTokenServices;

    @Autowired
    private RedisJwtTokenStore redisJwtTokenStore;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    //将认证服务加载到容器， 供 oauth2 的密码模式使用
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        //禁用csrf 使用前后端分离
        http.csrf().disable();
        //禁用session
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilter(new JwtAuthenticationFilter(authenticationManagerBean(), macSigner));
        http.authorizeRequests().antMatchers("/user/register", "/oauth2/github", "/testLogin").permitAll().anyRequest().authenticated().and().logout().logoutSuccessHandler(new LogoutSuccessHandler() {
            @Override
            public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                Authentication jwtAuthentication = JwtUtils.decodeToken(request, macSigner);
                String accessToken = JwtUtils.getAccessToken(request);
                if (jwtAuthentication != null && StringUtils.isNotBlank(accessToken)) {
                    String clintId = JwtUtils.getClientId(request, macSigner);
                    if (StringUtils.isNotBlank(clintId)) {
                        defaultTokenServices.revokeToken(accessToken);
                    } else {
                        redisJwtTokenStore.removeJwtToken(accessToken);
                    }
                }
            }
        }).and().formLogin()
                .failureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                        response.setStatus(HttpStatus.BAD_REQUEST.value());
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("error_description", exception.getMessage());
                        writeResp(response, jsonObject);
                    }
                }).successHandler(new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                String accessToken = UUID.randomUUID().toString();
                String jwtToken = GzdzssSecurityUtils.encodeToken(authentication, authExpiresInSeconds, macSigner);
                redisJwtTokenStore.storeJwtToken(accessToken, jwtToken, authExpiresInSeconds);
                JSONObject respJson = new JSONObject();
                respJson.put("access_token", accessToken);
                respJson.put("token_type", "bearer");
                respJson.put("expires_in", authExpiresInSeconds);
                writeResp(response, respJson);
            }
        }).and().exceptionHandling().accessDeniedHandler(new AccessDeniedHandler() {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("error_description", accessDeniedException.getMessage());
                writeResp(response, jsonObject);
            }
        }).authenticationEntryPoint(new AuthenticationEntryPoint() {
            @Override
            public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("error_description", authException.getMessage());
                writeResp(response, jsonObject);
            }
        });
    }


    private void writeResp(HttpServletResponse response, JSONObject jsonObject) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write(jsonObject.toJSONString());
        response.getWriter().flush();
    }


}
