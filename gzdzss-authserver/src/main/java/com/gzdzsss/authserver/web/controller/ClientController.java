package com.gzdzsss.authserver.web.controller;

import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:zhouyanjie666666@gmail.com">zyj</a>
 * @date 2019/4/22
 */
@Validated
@RestController
public class ClientController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping(value = "/client/list", method = RequestMethod.GET)
    public ResponseEntity list(Authentication authentication) {
        List<String> clientIds = jdbcTemplate.queryForList("SELECT client_id FROM `oauth_client_details` where username = ?", String.class, authentication.getName());
        return ResponseEntity.ok(clientIds);
    }


    @RequestMapping(value = "/client/detail/{clientId}", method = RequestMethod.GET)
    public ResponseEntity clients(@PathVariable String clientId, Authentication authentication) {
        List<Map<String, Object>> clientDetails = jdbcTemplate.queryForList("select  client_id ,  web_server_redirect_uri from oauth_client_details  where client_id = ? and username = ?", clientId, authentication.getName());
        return ResponseEntity.ok(clientDetails);
    }


    @RequestMapping(value = "/client/register", method = RequestMethod.POST)
    public ResponseEntity register(Authentication authentication, @RequestParam(value = "clientId") @Length(min = 4, max = 20) String clientId,
                                   @RequestParam(value = "clientSecret") @Length(min = 5, max = 20) String clientSecret, @RequestParam(value = "callbackUrl") String callbackUrl) {

        int count = jdbcTemplate.queryForObject("SELECT count(1) FROM `oauth_client_details` where client_id = ?", int.class, clientId);

        if (count > 0) {
            return ResponseEntity.badRequest().body("clientId: " + clientId + "已经被占用");
        }
        String scope = "login,userinfo";

        String pwd = passwordEncoder.encode(clientSecret);
        jdbcTemplate.update("insert into oauth_client_details (client_id,client_secret,web_server_redirect_uri,scope,authorized_grant_types,username) values(?,?,?,?,?,?)", clientId, pwd, callbackUrl, scope, "authorization_code,refresh_token,password,implicit,client_credentials", authentication.getName());
        return ResponseEntity.ok().build();
    }


}
