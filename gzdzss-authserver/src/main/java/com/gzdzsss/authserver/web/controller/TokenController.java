package com.gzdzsss.authserver.web.controller;

import com.gzdzsss.authserver.config.RedisJwtTokenStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

/**
 * @author <a href="mailto:zhouyanjie666666@gmail.com">zyj</a>
 * @date 2019/4/11
 */

@Slf4j
@RestController
public class TokenController {

    @Autowired
    private ConsumerTokenServices consumerTokenServices;

    @Autowired
    private RedisJwtTokenStore redisJwtTokenStore;

    @PreAuthorize("#oauth2.hasScope('login')")
    @RequestMapping(method = RequestMethod.POST, value = "/revoke")
    public ResponseEntity revokeToken(OAuth2Authentication auth2Authentication) {
        Collection<OAuth2AccessToken> totkens = redisJwtTokenStore.findTokensByClientIdAndUserName(auth2Authentication.getOAuth2Request().getClientId(), auth2Authentication.getName());
        for (OAuth2AccessToken token : totkens) {
            boolean result = consumerTokenServices.revokeToken(token.getValue());
            log.info("注销 token:[{}]:{}", token.getValue(), result);
        }
        return ResponseEntity.ok().build();
    }

}
