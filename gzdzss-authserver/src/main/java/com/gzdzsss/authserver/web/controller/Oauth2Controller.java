package com.gzdzsss.authserver.web.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.RedirectMismatchException;
import org.springframework.security.oauth2.common.exceptions.UnsupportedResponseTypeException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.endpoint.RedirectResolver;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    private AuthorizationServerTokenServices authorizationServerTokenServices;

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
            if (!StringUtils.hasText(resolvedRedirect)) {
                throw new RedirectMismatchException("A redirectUri must be either supplied or preconfigured in the ClientDetails");
            }
            return new AuthorizeVO(client, resolvedRedirect);
        } else {
            throw new InsufficientAuthenticationException("User must be authenticated with Spring Security before authorization can be completed.");
        }
    }


    @RequestMapping(value = "/oauth2/authorize/approve", method = RequestMethod.POST)
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
            OAuth2AccessToken accessToken = authorizationServerTokenServices.createAccessToken(combinedAuth);
            resp.put("access_token", accessToken.getValue());
            resp.put("token_type", accessToken.getTokenType());
            Date expiration = accessToken.getExpiration();
            if (expiration != null) {
                long expires_in = (expiration.getTime() - System.currentTimeMillis()) / 1000;
                resp.put("expires_in", expires_in);
            }
            resp.put("scope", OAuth2Utils.formatParameterList(accessToken.getScope()));

        } else if ("code".equals(responseType)) {
            String code = authorizationCodeServices.createAuthorizationCode(combinedAuth);
            resp.put("code", code);
        }
        resp.put("redirectUri", redirectUri);
        resp.put("state", state);
        return ResponseEntity.ok(resp);
    }

    @AllArgsConstructor
    @Data
    protected class AuthorizeVO {
        private ClientDetails clientDetails;
        private String resolvedRedirect;
    }

}
