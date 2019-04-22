package com.gzdzss.order.web.contoller;

import com.gzdzss.order.api.StorageApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="mailto:zhouyanjie666666@gmail.com">zyj</a>
 * @date 2019/4/3
 */


@Slf4j
@RestController
@RequestMapping(value = "/api")
public class OrderController {


    @Autowired
    private StorageApi storageApi;

    @GetMapping(value = "/order/test")
    public ResponseEntity test(OAuth2Authentication oAuth2Authentication) {
        return ResponseEntity.ok().body(oAuth2Authentication.getOAuth2Request().getScope());
    }


    @PreAuthorize("#oauth2.hasScope('login') and #oauth2.hasScope('userinfo')")
    @GetMapping(value = "/order/list")
    public ResponseEntity list() {
        return ResponseEntity.ok().body("list" + RandomStringUtils.randomNumeric(8));
    }


    @PreAuthorize("#oauth2.hasScope('xxx')")
    @GetMapping(value = "/order/xxx")
    public ResponseEntity xxx() {
        return ResponseEntity.ok().body("xxx");
    }


    @PreAuthorize("#oauth2.hasScope('login')")
    @RequestMapping(value = "/order/buy", method = RequestMethod.POST)
    public ResponseEntity buy(Authentication authentication) {
        log.info("{}下单， 扣减库存", authentication.getName());
        //扣减库存
        String resp = storageApi.stock(Integer.valueOf(RandomStringUtils.randomNumeric(1)));
        return ResponseEntity.ok().body(resp);
    }

}
