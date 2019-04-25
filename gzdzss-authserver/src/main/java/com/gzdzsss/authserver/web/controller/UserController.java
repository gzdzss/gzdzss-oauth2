package com.gzdzsss.authserver.web.controller;

import com.gzdzsss.authserver.jpa.entity.Authorities;
import com.gzdzsss.authserver.jpa.entity.User;
import com.gzdzsss.authserver.jpa.repository.UserRepository;
import com.gzdzsss.authserver.util.RespUtils;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author <a href="mailto:zhouyanjie666666@gmail.com">zyj</a>
 * @date 2019/4/22
 */

@RestController
@Validated
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @RequestMapping(method = RequestMethod.GET, path = "/user")
    public ResponseEntity test(Authentication authentication) {
        return ResponseEntity.ok(authentication.getPrincipal());
    }


    @RequestMapping(method = RequestMethod.POST, value = "/user/register")
    public ResponseEntity register(@RequestParam(value = "username") @Length(min = 4, max = 20) String username,
                                   @RequestParam(value = "password") @Length(min = 6, max = 20) String password) {

        Optional<User> optionalUser =  userRepository.findByUsername(username);

        if (optionalUser.isPresent()) {
            return RespUtils.respError("用户名已存在");
        }

        User user  = new User();
        user.setUsername(username);
        user.setNickName(username);
        user.setAvatarUrl("https://cdn.gzdzss.cn/static/home/logo.png");
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true);

        List<Authorities> authorities = new ArrayList<>(1);
        authorities.add(new Authorities("USER"));
        user.setAuthoritiesList(authorities);

        userRepository.save(user);

        return ResponseEntity.ok().build();
    }


}
