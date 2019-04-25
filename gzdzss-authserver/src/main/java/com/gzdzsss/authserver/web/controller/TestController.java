package com.gzdzsss.authserver.web.controller;

import com.gzdzss.security.util.GzdzssSecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="mailto:zhouyanjie666666@gmail.com">zyj</a>
 * @date 2019/4/19
 */

@Slf4j
@RestController
public class TestController {


    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(method = RequestMethod.GET, path = "/test")
    public ResponseEntity test(Authentication jwtAuthentication) {
        log.info("user:{}", GzdzssSecurityUtils.getUser());
        return ResponseEntity.ok(jwtAuthentication.getPrincipal());
    }

    @RequestMapping(method = RequestMethod.GET, path = "/adminTest")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity adminTest(Authentication jwtAuthentication) {
        return ResponseEntity.ok(jwtAuthentication.getPrincipal());
    }

    @RequestMapping(method = RequestMethod.GET, path = "/testLogin")
    public ResponseEntity testLogin() {
        return ResponseEntity.ok(GzdzssSecurityUtils.getUser());
    }


}
