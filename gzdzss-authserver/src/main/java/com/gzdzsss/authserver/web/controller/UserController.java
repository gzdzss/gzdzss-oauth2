package com.gzdzsss.authserver.web.controller;

import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author <a href="mailto:zhouyanjie666666@gmail.com">zyj</a>
 * @date 2019/4/22
 */

@RestController
@Validated
public class UserController {

    @Autowired
    private UserDetailsManager userDetailsManager;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @RequestMapping(method = RequestMethod.POST, value = "/user/register")
    public ResponseEntity register(@RequestParam(value = "username") @Length(min = 4, max = 20) String username,
                                   @RequestParam(value = "password") @Length(min = 6, max = 20) String password) {
        if (userDetailsManager.userExists(username)) {
            return ResponseEntity.badRequest().body("用户名已存在");
        }
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("USER");
        UserDetails userDetails = new User(username, passwordEncoder.encode(password), authorities);
        userDetailsManager.createUser(userDetails);
        return ResponseEntity.ok().build();
    }


}
