package com.gzdzsss.authserver.config.jwt;

import com.gzdzss.security.util.GzdzssSecurityUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.jwt.crypto.sign.MacSigner;

import javax.servlet.http.HttpServletRequest;

/**
 * @author <a href="mailto:zhouyanjie666666@gmail.com">zyj</a>
 * @date 2019/4/24
 */

public class JwtUtils {

    public static Authentication decodeToken(HttpServletRequest request, MacSigner macSigner) {
        String header = request.getHeader("authorization");
        if (header != null && header.toLowerCase().startsWith("bearer ")) {
            String token = header.substring(7);
            return GzdzssSecurityUtils.decodeToken(token, macSigner);
        }
        return null;
    }
}
