package com.gzdzss.security;

import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;

/**
 * @author <a href="mailto:zhouyanjie666666@gmail.com">zyj</a>
 * @date 2019/4/24
 */

public class GzdzssAccessTokenConverter extends DefaultAccessTokenConverter {


    public GzdzssAccessTokenConverter() {
        this.setUserTokenConverter(new GzdzssUserAuthenticationConverter());
    }
}
