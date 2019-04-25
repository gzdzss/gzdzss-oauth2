package com.gzdzsss.authserver.config.oauth;

import com.gzdzss.security.GzdzssAccessTokenConverter;
import com.gzdzss.security.util.GzdzssSecurityUtils;
import com.gzdzsss.authserver.config.jwt.JwtConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStoreSerializationStrategy;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 将accessToken存储到redis, 同时生成jwtToken供微服务之间内部调用
 *
 * @author <a href="mailto:zhouyanjie666666@gmail.com">zyj</a>
 * @date 2019/3/30
 */

@Component
public class RedisJwtTokenStore extends RedisTokenStore {

    private RedisConnectionFactory connectionFactory;

    private RedisTokenStoreSerializationStrategy serializationStrategy;

    private MacSigner macSigner;

    @Autowired
    public RedisJwtTokenStore(RedisConnectionFactory connectionFactory, RedisConnectionFactory connectionFactory1, RedisTokenStoreSerializationStrategy serializationStrategy, MacSigner macSigner) {
        super(connectionFactory);
        this.connectionFactory = connectionFactory1;
        this.serializationStrategy = serializationStrategy;
        this.macSigner = macSigner;
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
        return serializationStrategy.serialize(JwtConstant.JWT_TO_ACCESS + tokenValue);
    }


    private void storeJwtToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        byte[] key = serializeJwtKey(token.getValue());

        GzdzssAccessTokenConverter converter = new GzdzssAccessTokenConverter();
        Map<String, ?> map = converter.convertAccessToken(token, authentication);

        String jwtToken = GzdzssSecurityUtils.encodeToken(map, macSigner);

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
