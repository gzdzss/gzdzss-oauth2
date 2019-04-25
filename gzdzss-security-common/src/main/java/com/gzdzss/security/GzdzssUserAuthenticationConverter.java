package com.gzdzss.security;

import com.alibaba.fastjson.JSON;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:zhouyanjie666666@gmail.com">zyj</a>
 * @date 2019/4/24
 */

public class GzdzssUserAuthenticationConverter implements UserAuthenticationConverter {
    private static final String PRINCIPAL = "principal";

    @Override
    public Map<String, ?> convertUserAuthentication(Authentication authentication) {
        GzdzssUserDetails gzdzssUserDetails = (GzdzssUserDetails) authentication.getPrincipal();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put(AUTHORITIES, String.join(",", AuthorityUtils.authorityListToSet(gzdzssUserDetails.getAuthorities())));
        //密码与权限 不存入  principal json
        gzdzssUserDetails.setPassword(null);
        gzdzssUserDetails.setAuthorities(null);
        response.put(PRINCIPAL, JSON.toJSONString(gzdzssUserDetails));
        return response;
    }


    @Override
    public Authentication extractAuthentication(Map<String, ?> map) {
        if (map.containsKey(PRINCIPAL)) {
            GzdzssUserDetails gzdzssUserDetails = JSON.parseObject(map.get(PRINCIPAL).toString(), GzdzssUserDetails.class);
            List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(String.join(",", map.get(AUTHORITIES).toString()));
            gzdzssUserDetails.setAuthorities(new HashSet<>(authorities));
            return new UsernamePasswordAuthenticationToken(gzdzssUserDetails, gzdzssUserDetails.getPassword(), authorities);
        }
        return null;

    }


}
