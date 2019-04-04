package com.gzdzsss.authserver.config;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.Signer;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStoreSerializationStrategy;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 将accessToken存储到redis, 同时生成jwtToken供微服务之间内部调用
 *
 * @author <a href="mailto:zhouyanjie666666@gmail">zyj</a>
 * @date 2019/3/30
 */

@Component
public class RedisJwtTokenStore extends RedisTokenStore {

    private static final String JWT_TO_ACCESS = "jwt_to_access:";

    private RedisConnectionFactory connectionFactory;

    private RedisTokenStoreSerializationStrategy serializationStrategy;

    private JwtAccessTokenConverter jwtAccessTokenConverter;

    private Signer signer;

    @Autowired
    public RedisJwtTokenStore(RedisConnectionFactory connectionFactory, RedisConnectionFactory connectionFactory1, RedisTokenStoreSerializationStrategy serializationStrategy, Signer signer,JwtAccessTokenConverter jwtAccessTokenConverter) {
        super(connectionFactory);
        this.connectionFactory = connectionFactory1;
        this.serializationStrategy = serializationStrategy;
        this.signer = signer;
        this.jwtAccessTokenConverter = jwtAccessTokenConverter;
    }


    @Override
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        //保存jwtToken
        storeJwtToken(token, authentication);
        //保存accessToken
        super.storeAccessToken(token, authentication);
    }

    @Override
    public void removeAccessToken(String tokenValue) {
        //删除 jwtToken
        removeJwtToken(tokenValue);
        //删除accessToken
        super.removeAccessToken(tokenValue);
    }


    private byte[] serializeJwtKey(String tokenValue) {
        return serializationStrategy.serialize(JWT_TO_ACCESS + tokenValue);
    }


    private void storeJwtToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        byte[] key = serializeJwtKey(token.getValue());

        Map<String, ?> convertAccessToken = jwtAccessTokenConverter.convertAccessToken(token, authentication);

        String jwtToken = JwtHelper.encode(JSON.toJSONString(convertAccessToken), signer).getEncoded();

        byte[] val = serializationStrategy.serialize(jwtToken);

        connectionFactory.getConnection().set(key, val);

        if (token.getExpiration() != null) {
            int seconds = token.getExpiresIn();
            connectionFactory.getConnection().expire(key, seconds);
        }
    }


    private void removeJwtToken(String tokenValue) {
        byte[] key = serializeJwtKey(tokenValue);
        connectionFactory.getConnection().del(key);
    }


}
