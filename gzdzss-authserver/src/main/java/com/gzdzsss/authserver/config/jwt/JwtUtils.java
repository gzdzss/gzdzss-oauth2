package com.gzdzsss.authserver.config.jwt;

import com.gzdzss.security.util.GzdzssSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.jwt.crypto.sign.MacSigner;

import javax.servlet.http.HttpServletRequest;

/**
 * @author <a href="mailto:zhouyanjie666666@gmail.com">zyj</a>
 * @date 2019/4/24
 */

public class JwtUtils {

    private static final String HEADER_ACCESS_TOKEN = "accessToken";
    private static final String AUTHORIZATION = "authorization";
    private static final String BEARER = "bearer ";
    private static final int BEARER_LENGTH = 7;


    public static Authentication decodeToken(HttpServletRequest request, MacSigner macSigner) {
        String token = getToken(request);
        if (StringUtils.isNotBlank(token)) {
            return GzdzssSecurityUtils.decodeToken(token, macSigner);
        }
        return null;
    }


    private static String getToken(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION);
        if (header != null && header.toLowerCase().startsWith(BEARER)) {
            return header.substring(BEARER_LENGTH);
        }
        return null;
    }


    public static String getClientId(HttpServletRequest request, MacSigner macSigner) {
        String token = getToken(request);
        if (StringUtils.isNotBlank(token)) {
            return GzdzssSecurityUtils.getClientId(token, macSigner);
        }
        return null;
    }


    public static String getAccessToken(HttpServletRequest request) {
        return request.getHeader(HEADER_ACCESS_TOKEN);
    }
}
