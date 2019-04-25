package com.gzdzsss.authserver.web.controller;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.RedirectMismatchException;
import org.springframework.security.oauth2.common.exceptions.UnsupportedResponseTypeException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.endpoint.RedirectResolver;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:zhouyanjie666666@gmail.com">zyj</a>
 * @date 2019/4/19
 */
@RestController
public class Oauth2Controller {

    @Autowired
    private RedirectResolver redirectResolver;

    @Autowired
    private ClientDetailsService clientDetailsService;

    @Autowired
    private OAuth2RequestValidator oAuth2RequestValidator;

    @Autowired
    private AuthorizationCodeServices authorizationCodeServices;

    @Autowired
    private DefaultTokenServices defaultTokenServices;

    @Autowired
    public RestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserDetailsService userDetailsService;

    @RequestMapping(value = "/oauth2/authorize", method = RequestMethod.GET)
    public ResponseEntity authorize(@RequestParam(value = OAuth2Utils.CLIENT_ID) String clientId,
                                    @RequestParam(value = OAuth2Utils.REDIRECT_URI) String redirectUri,
                                    @RequestParam(value = OAuth2Utils.RESPONSE_TYPE) String responseType,
                                    Authentication authentication) {
        AuthorizeVO authorizeVO = validate(authentication, responseType, clientId, redirectUri);
        Map<String, Object> resp = new HashMap<>();
        resp.put(OAuth2Utils.CLIENT_ID, authorizeVO.getClientDetails().getClientId());
        resp.put(OAuth2Utils.RESPONSE_TYPE, responseType);
        resp.put(OAuth2Utils.REDIRECT_URI, authorizeVO.getResolvedRedirect());
        resp.put(OAuth2Utils.SCOPE, authorizeVO.getClientDetails().getScope());
        return ResponseEntity.ok(resp);
    }


    private AuthorizeVO validate(Authentication authentication, String responseType, String clientId, String redirectUri) {
        if (!"token".equals(responseType) && !"code".equals(responseType)) {
            throw new UnsupportedResponseTypeException("Unsupported response types: " + responseType);
        }
        if (authentication.isAuthenticated()) {
            ClientDetails client = clientDetailsService.loadClientByClientId(clientId);
            //解析回调地址
            String resolvedRedirect = redirectResolver.resolveRedirect(redirectUri, client);
            if (StringUtils.isBlank(resolvedRedirect)) {
                throw new RedirectMismatchException("A redirectUri must be either supplied or preconfigured in the ClientDetails");
            }
            return new AuthorizeVO(client, resolvedRedirect);
        } else {
            throw new InsufficientAuthenticationException("User must be authenticated with Spring Security before authorization can be completed.");
        }
    }


    @RequestMapping(value = "/oauth2/approve", method = RequestMethod.POST)
    public ResponseEntity authorize2(@RequestParam(value = OAuth2Utils.CLIENT_ID) String clientId,
                                     @RequestParam(value = OAuth2Utils.REDIRECT_URI) String redirectUri,
                                     @RequestParam(value = OAuth2Utils.RESPONSE_TYPE) String responseType,
                                     @RequestParam(value = OAuth2Utils.STATE, required = false) String state,
                                     @RequestParam(value = OAuth2Utils.SCOPE) Set<String> scope,
                                     Authentication authentication) {
        AuthorizeVO authorizeVO = validate(authentication, responseType, clientId, redirectUri);
        AuthorizationRequest request = new AuthorizationRequest();
        request.setClientId(clientId);
        request.setState(state);
        request.setRedirectUri(redirectUri);
        request.setResponseTypes(OAuth2Utils.parseParameterList(responseType));
        request.setScope(scope);
        oAuth2RequestValidator.validateScope(request, authorizeVO.getClientDetails());

        request.setResourceIdsAndAuthoritiesFromClientDetails(authorizeVO.getClientDetails());
        OAuth2Authentication combinedAuth = new OAuth2Authentication(request.createOAuth2Request(), authentication);

        Map<String, Object> resp = new HashMap<>();

        if ("token".equals(responseType)) {
            OAuth2AccessToken accessToken = defaultTokenServices.createAccessToken(combinedAuth);
            resp.put("access_token", accessToken.getValue());
            resp.put("token_type", accessToken.getTokenType());
            resp.put("refresh_token", accessToken.getRefreshToken().getValue());
            Date expiration = accessToken.getExpiration();
            if (expiration != null) {
                long expiresIn = (expiration.getTime() - System.currentTimeMillis()) / 1000;
                resp.put("expires_in", expiresIn);
            }
            resp.put("scope", OAuth2Utils.formatParameterList(accessToken.getScope()));
        } else if ("code".equals(responseType)) {
            String code = authorizationCodeServices.createAuthorizationCode(combinedAuth);
            resp.put("code", code);
        }
        resp.put("response_type", responseType);
        resp.put("redirect_uri", redirectUri);
        resp.put("state", state);
        return ResponseEntity.ok(resp);
    }


    @RequestMapping(value = "/oauth2/github", method = RequestMethod.GET)
    public ResponseEntity github(@RequestParam(value = "code") String code) {
            RestTemplate restTemplate = new RestTemplate();
            String user = "3454267794e0ec293968";
            String password = "f038263d3e2a199c0c639008b8629a8ae9a5fe82";
            String userMsg = user + ":" + password;
            String base64UserMsg = Base64.encodeBase64String(userMsg.getBytes());
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Basic " + base64UserMsg);
            HttpEntity httpEntity = new HttpEntity(headers);
            ResponseEntity<Map> resp = restTemplate.exchange("https://github.com/login/oauth/access_token?code="+code, HttpMethod.GET, httpEntity, Map.class);
            Map<String, String> map = resp.getBody();
            String error = map.get("error");
            if (StringUtils.isNotBlank(error)) {
                return ResponseEntity.badRequest().body(map);
            } else {
                String accessToken = map.get("access_token");
                HttpHeaders userHeaders = new HttpHeaders();
                userHeaders.add("Authorization", "token " +accessToken);
                ResponseEntity<Map> respUser = restTemplate.exchange("https://api.github.com/user", HttpMethod.GET, httpEntity, Map.class);
                if (respUser.getStatusCode() == HttpStatus.OK) {
                    Map<String, String> userMap = respUser.getBody();
                    String id = userMap.get("id");
                    String login = userMap.get("login");
                    String avatarUrl=  userMap.get("avatar_url");
                    String name = userMap.get("name");
                    //根据id查询 是否已经绑定过，   有则直接查询返回，  没有则提示


                    String username =  jdbcTemplate.queryForObject("select username from users where github_id = ?", String.class,id );

                    if (StringUtils.isBlank(username)) {
                        return ResponseEntity.badRequest().body(userMap);
                    }

                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);


                    return null;

                } else {
                    return respUser;
                }
        }
    }


    @AllArgsConstructor
    @Data
    protected class AuthorizeVO {
        private ClientDetails clientDetails;
        private String resolvedRedirect;
    }

}
