

#网关



##1.代理认证中心

可通过 访问鉴权中心
http://localhost:7001/auth 


##2.代理订单服务

订单：
http://localhost:7001/order



## 2.1 获取订单列表

GET : http://localhost:7001/order/api/order/list
Headers: Authorization: Bearer 065f3fa8-41be-434d-92c7-8b1fa63bc004

响应：
正常

GET : http://localhost:7001/order/api/order/xxx
Headers: Authorization: Bearer 065f3fa8-41be-434d-92c7-8b1fa63bc004

响应：

```josn
{
    "error": "insufficient_scope",
    "error_description": "Insufficient scope for this resource",
    "scope": "xxx"
}
```

## 3代理库存

http://localhost:7001/storage


