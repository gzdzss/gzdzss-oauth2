#库存模块


##查询库存列表 （校验权限）
/api/storage/list


##扣减库存 （提供给 订单 feign 调用， 配置熔断）
/api/storage/stock
 