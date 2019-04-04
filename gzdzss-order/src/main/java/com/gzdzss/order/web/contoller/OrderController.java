package com.gzdzss.order.web.contoller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author <a href="mailto:zhouyanjie666666@gmail">zyj</a>
 * @date 2019/4/3
 */


@RestController
public class OrderController {

    @PreAuthorize("#oauth2.hasScope('login') and #oauth2.hasScope('userinfo')")
    @GetMapping(value = "/api/order/list")
    public ResponseEntity list(HttpServletRequest request) {
        return ResponseEntity.ok().body("list");
    }


    @PreAuthorize("#oauth2.hasScope('xxx')")
    @GetMapping(value = "/api/order/xxx")
    public ResponseEntity xxx() {
        return ResponseEntity.ok().body("xxx");
    }


}
