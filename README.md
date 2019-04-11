# oauth2整合

## eureka-server  注册中心     

## gzdzss-order 订单模块

## gzdzss-storage 库存模块

## gzdzss-authserver 认证中心

## gzdzss-gateway 服务网关

 

## 架构图

![image](https://github.com/gzdzss/gzdzss-oauth2/raw/master/oauth2.png)


## 分支

### [1.0.0.RELEASE](https://github.com/gzdzss/gzdzss-oauth2/tree/1.0.0.RELEASE) 单一版（无注册中心）


### [2.0.0.RELEASE](https://github.com/gzdzss/gzdzss-oauth2/tree/2.0.0.RELEASE) 集群版本 （添加注册中心， feign, hystrix）


##配套前台demo页面  （vue 2.0,  vuex , vue-route, axios）

[gzdzss-web](https://github.com/gzdzss/gzdzss-web)

###登录
###自动刷新token  (配置认证中心 oauth_client_details (access_token_validity)  设置为60s,可验证每次先刷新再调用)
###注销
###接口调用



