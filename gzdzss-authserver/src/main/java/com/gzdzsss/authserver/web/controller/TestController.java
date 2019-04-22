package com.gzdzsss.authserver.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="mailto:zhouyanjie666666@gmail.com">zyj</a>
 * @date 2019/4/19
 */

@RestController
public class TestController {


    @RequestMapping(method = RequestMethod.GET, path = "/test")
    public ResponseEntity test() {
        return ResponseEntity.ok("ok");
    }

}
