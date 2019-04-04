package com.gzdzss.order.api;

import com.gzdzss.order.config.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author <a href="mailto:zhouyanjie666666@gmail">zyj</a>
 * @date 2019/4/4
 */

@FeignClient(name = "storage", configuration = FeignConfiguration.class)
public interface StorageApi {


    @PostMapping("/api/storage/stock")
    String stock(@RequestParam("stock") Integer stock);
}
