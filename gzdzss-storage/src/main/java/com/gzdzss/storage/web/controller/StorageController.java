package com.gzdzss.storage.web.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="mailto:zhouyanjie666666@gmail">zyj</a>
 * @date 2019/4/4
 */

@Slf4j
@RestController
@RequestMapping(value = "/api")
public class StorageController {

    @PreAuthorize("#oauth2.hasScope('userinfo')")
    @GetMapping(value = "/storage/list")
    public ResponseEntity list() {
        return ResponseEntity.ok().body("storage list");
    }


    @HystrixCommand(
            commandProperties = { // Command 配置
                    // 设置操作时间为 100 毫秒
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000")
            },
            fallbackMethod = "fallbackForStock" // 设置 fallback 方法
    )
    @PreAuthorize("#oauth2.hasScope('userinfo')")
    @RequestMapping(value = "/storage/stock")
    public ResponseEntity stock(@RequestParam(value = "stock") Integer stock) throws InterruptedException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("用户：{}， 扣减库存:{}", username, stock);

        if (stock > 5) {
            Thread.sleep(1200);
        }

        return ResponseEntity.ok().body("成功");
    }

    public ResponseEntity fallbackForStock(@RequestParam(value = "stock") Integer stock) {
        log.info("熔断");
        return ResponseEntity.ok().body("服务端：请求太火爆了，请稍后再试");
    }



}
