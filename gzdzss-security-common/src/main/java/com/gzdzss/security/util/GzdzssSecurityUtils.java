package com.gzdzss.security.util;

import com.alibaba.fastjson.JSON;
import com.gzdzss.security.GzdzssUserAuthenticationConverter;
import com.gzdzss.security.GzdzssUserDetails;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.MacSigner;

import java.util.Map;

/**
 * @author <a href="mailto:zhouyanjie666666@gmail">zyj</a>
 * @date 2019/4/2
 */

public class GzdzssSecurityUtils {

    private final static GzdzssUserAuthenticationConverter CONVERTER = new GzdzssUserAuthenticationConverter();





    public static Map convertUserAuthentication(Authentication authentication) {
        return CONVERTER.convertUserAuthentication(authentication);
    }

    public static String encodeToken(Authentication authentication, long authExpiresInSeconds, MacSigner macSigner) {
        Map map = convertUserAuthentication(authentication);
        map.put("exp", (System.currentTimeMillis() / 1000) + authExpiresInSeconds);
        return encodeToken(map, macSigner);
    }

    public static String encodeToken(Map map, MacSigner macSigner) {
        Jwt jwt = JwtHelper.encode(JSON.toJSONString(map), macSigner);
        return jwt.getEncoded();
    }





    public static String getClientId(String token, MacSigner macSigner) {
        Map map = decodeTokenToMap(token, macSigner);
        if (map.get("client_id") != null) {
            return  map.get("client_id").toString();
        }
        return null;
    }

    private static Map decodeTokenToMap(String token, MacSigner macSigner) {
        Jwt jwt = JwtHelper.decodeAndVerify(token, macSigner);
        return JSON.parseObject(jwt.getClaims(), Map.class);
    }

    public static Authentication decodeToken(String token, MacSigner macSigner) {
        Map map = decodeTokenToMap(token, macSigner);
        Long exp = Long.valueOf(map.get("exp").toString());
        //如果token过期
        if ((System.currentTimeMillis() / 1000) > exp) {
            return null;
        }
        return CONVERTER.extractAuthentication(map);
    }


    public static GzdzssUserDetails getUser() throws AuthenticationException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof GzdzssUserDetails) {
            return (GzdzssUserDetails) principal;
        }
        throw new InsufficientAuthenticationException("还未登录");
    }

}
