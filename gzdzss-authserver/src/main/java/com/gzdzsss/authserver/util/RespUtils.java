package com.gzdzsss.authserver.util;

import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:zhouyanjie666666@gmail.com">zyj</a>
 * @date 2019/4/25
 */

public class RespUtils {

    public static ResponseEntity respError(String msg) {
        Map<String, String> map = new HashMap<>(1);
        map.put("error_description", msg);
        return ResponseEntity.badRequest().body(map);
    }
}
