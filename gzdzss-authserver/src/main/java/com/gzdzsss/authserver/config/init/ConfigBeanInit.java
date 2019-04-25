package com.gzdzsss.authserver.config.init;

import com.gzdzsss.authserver.config.oauth.RedisJwtTokenStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.oauth2.config.annotation.builders.ClientDetailsServiceBuilder;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2RequestValidator;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.endpoint.DefaultRedirectResolver;
import org.springframework.security.oauth2.provider.endpoint.RedirectResolver;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestValidator;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.store.redis.JdkSerializationStrategy;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStoreSerializationStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;

/**
 * @author <a href="mailto:zhouyanjie666666@gmail.com">zyj</a>
 * @date 2019/4/2
 */

@Component
public class ConfigBeanInit {


    //密码加密方式
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }




    //授权码管理的方法
    @Bean
    public AuthorizationCodeServices authorizationCodeServices(DataSource dataSource) {
        return new JdbcAuthorizationCodeServices(dataSource);
    }

    //认证token服务
    @Bean
    public DefaultTokenServices defaultTokenServices(ClientDetailsService clientDetailsService, RedisJwtTokenStore redisJwtTokenStore) {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setClientDetailsService(clientDetailsService);
        defaultTokenServices.setTokenStore(redisJwtTokenStore);
        defaultTokenServices.setReuseRefreshToken(true);
        return defaultTokenServices;
    }


    //客户端管理的方法
    @Bean
    public ClientDetailsServiceBuilder clientDetailsServiceBuilder(DataSource dataSource, PasswordEncoder passwordEncoder) throws Exception {
        return new ClientDetailsServiceBuilder().jdbc().dataSource(dataSource).passwordEncoder(passwordEncoder);
    }


    //重定向解析器
    @Bean
    public RedirectResolver redirectResolver() {
        return new DefaultRedirectResolver();
    }


    //Redis序列化的方法
    @Bean
    public RedisTokenStoreSerializationStrategy serializationStrategy() {
        return new JdkSerializationStrategy();
    }


    @Bean
    private MacSigner macSigner(@Value("${jwt.signingKey}") String signingKey) {
        return new MacSigner(signingKey);
    }


    @Bean
    public OAuth2RequestValidator oAuth2RequestValidator() {
        return new DefaultOAuth2RequestValidator();
    }


    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


}
