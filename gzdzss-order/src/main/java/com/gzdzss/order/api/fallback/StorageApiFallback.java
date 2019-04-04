package com.gzdzss.order.api.fallback;

import com.gzdzss.order.api.StorageApi;

/**
 * @author <a href="mailto:zhouyanjie666666@gmail">zyj</a>
 * @date 2019/4/4
 */

public class StorageApiFallback implements StorageApi {
    @Override
    public String stock(Integer stock) {
        return "客户端：请求太火爆啦，请稍等一会";
    }
}
