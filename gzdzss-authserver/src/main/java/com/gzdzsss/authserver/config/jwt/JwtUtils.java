package com.gzdzsss.authserver.config.jwt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.SignerVerifier;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:zhouyanjie666666@gmail">zyj</a>
 * @date 2019/4/2
 */

public class JwtUtils {

    private static final String USERNAME = "user_name";
    private static final String AUTHORITIES = "authorities";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String CLIENT_ID = "client_id";


    public static String encodeToken(String accessToken, Authentication authentication, SignerVerifier signerVerifier) {
        Map<String, Object> content = new HashMap<>();
        content.put(ACCESS_TOKEN, accessToken);
        content.put(USERNAME, authentication.getName());
        if (authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
            content.put(AUTHORITIES, AuthorityUtils.authorityListToSet(authentication.getAuthorities()));
        }
        String token = JwtHelper.encode(JSON.toJSONString(content), signerVerifier).getEncoded();
        return token;
    }


    public static JwtAuthentication decodeToken(String token, SignerVerifier signerVerifier) {
        Jwt jwt = JwtHelper.decodeAndVerify(token, signerVerifier);

        JSONObject claims = JSON.parseObject(jwt.getClaims());

        String username = claims.getString(USERNAME);

        List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(claims.getString(AUTHORITIES));

        String accessToken = claims.getString(ACCESS_TOKEN);

        String clientId = claims.getString(CLIENT_ID);

        return new JwtAuthentication(accessToken, clientId, username, authorities);
    }


    public static JwtAuthentication decodeToken(HttpServletRequest request, SignerVerifier signerVerifier) {
        String header = request.getHeader("authorization");
        if (header != null && header.toLowerCase().startsWith("bearer ")) {
            String token = header.substring(7);
            return JwtUtils.decodeToken(token, signerVerifier);
        }
        return null;
    }

}
