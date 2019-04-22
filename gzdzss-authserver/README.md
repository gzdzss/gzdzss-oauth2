#认证中心


## 1.环境
### 1.1 redis

docker run --name redis  -p 6379:6379 -d  redis:5  --requirepass "gzdzssredispassword" 


### 1.2 mysql
docker run --name mysql -v  data:/var/lib/mysql   -p 3306:3306  -e MYSQL_ROOT_PASSWORD=123456 -d mysql:5.7
 


```sql
##创建数据库
CREATE DATABASE authserver CHARACTER SET utf8mb4;


CREATE TABLE `users` (
  `username` varchar(20) NOT NULL,
  `password` varchar(256) NOT NULL,
  `enabled` tinyint(1) NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `authorities` (
  `username` varchar(255) DEFAULT NULL,
  `authority` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE `oauth_code` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) DEFAULT NULL,
  `authentication` blob,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;


CREATE TABLE `oauth_client_details` (
  `client_id` varchar(256) NOT NULL,
  `resource_ids` varchar(256) DEFAULT NULL,
  `client_secret` varchar(256) NOT NULL,
  `scope` varchar(256) DEFAULT NULL,
  `authorized_grant_types` varchar(256) DEFAULT NULL,
  `web_server_redirect_uri` varchar(256) DEFAULT NULL,
  `authorities` varchar(256) DEFAULT NULL,
  `access_token_validity` int(11) DEFAULT NULL COMMENT '默认有效期,默认:43200',
  `refresh_token_validity` int(11) DEFAULT NULL COMMENT '默认刷新有效期:2592000',
  `additional_information` varchar(4096) DEFAULT NULL,
  `autoapprove` varchar(256) DEFAULT NULL,
  `username` varchar(20) NOT NULL,
  PRIMARY KEY (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


```


### 1.3注册用户：
http://localhost:8888/auth/user/register     例： user/123456

### 1.4.查看客户端
http://localhost:8888/auth/client/list
http://localhost:8888/auth/client/detail/{id}

### 1.5.注册客户端
http://localhost:8888/auth/client/register  例如;  aaaaa  bbbbb  https://www.baidu.com/



## 2.申请授权token

###2.1 授权码模式  （response_type=code）

浏览器请求：
http://localhost:8888/auth/oauth/authorize?client_id=aaaaa&redirect_uri=https://www.baidu.com/&response_type=code&scope=login userinfo

响应案例：
https://www.baidu.com/?code=77Up9V

获取访问令牌

POSTMAN:
POST:
http://localhost:8888/auth/oauth/token?grant_type=authorization_code&code=77Up9V&redirect_uri=https://www.baidu.com/


Authorization
Baseic Auth:
Username: aaaaa
Password: bbbbb

响应案例：
```json
{
    "access_token": "e3251089-a7e1-4794-941d-f74d9146a415",
    "token_type": "bearer",
    "refresh_token": "7d62b23e-b7ac-4441-b5a9-3c630518df77",
    "expires_in": 43199,
    "scope": "login userinfo"
}

```


###2.2 客户端模式  （token中没有用户信息）

POST: http://localhost:8888/auth/oauth/token?grant_type=client_credentials
Authorization
Baseic Auth:
Username: aaaaa
Password: bbbbb

响应：
```json
{
    "access_token": "5bd8e340-929d-44ca-9684-1369fa509ebe",
    "token_type": "bearer",
    "expires_in": 43199,
    "scope": "login userinfo"
}

```


###2.3 简化模式 （response_type=token）

浏览器请求：
http://localhost:8888/auth/oauth/authorize?client_id=aaaaa&redirect_uri=https://www.baidu.com/&response_type=token&scope=login%20userinfo&state=abc


响应：
https://www.baidu.com/#access_token=e3251089-a7e1-4794-941d-f74d9146a415&token_type=bearer&state=abc&expires_in=43137


###2.4 密码模式

POST http://localhost:8888/auth/oauth/token?grant_type=password&username=user&password=123456&scope=login userinfo

Authorization
Baseic Auth:
Username: aaaaa
Password: bbbbb

响应案例：
```json
{
    "access_token": "e3251089-a7e1-4794-941d-f74d9146a415",
    "token_type": "bearer",
    "refresh_token": "7d62b23e-b7ac-4441-b5a9-3c630518df77",
    "expires_in": 43098,
    "scope": "login userinfo"
}
```



###2.5刷新令牌


POST: http://localhost:8888/auth/oauth/token?grant_type=refresh_token&refresh_token=7d62b23e-b7ac-4441-b5a9-3c630518df77

Authorization
Baseic Auth:
Username: aaaaa
Password: bbbbb

响应案例：

```json
{
    "access_token": "065f3fa8-41be-434d-92c7-8b1fa63bc004",
    "token_type": "bearer",
    "refresh_token": "7d62b23e-b7ac-4441-b5a9-3c630518df77",
    "expires_in": 43199,
    "scope": "login userinfo"
}


```




## 3.查看内部jwtToken

通过RedisDesktopManager 查找

key:  jwt_to_access:065f3fa8-41be-434d-92c7-8b1fa63bc004

val:  eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1NTQzODg4NzYsInVzZXJfbmFtZSI6InVzZXIiLCJhdXRob3JpdGllcyI6WyJVU0VSIl0sImNsaWVudF9pZCI6ImFhYWFhIiwic2NvcGUiOlsibG9naW4iLCJ1c2VyaW5mbyJdfQ.iaG1-dG4mE8HPNp5QeUJjB2sAuzeya_ginole4aKkcA

可通过 https://jwt.io/ 
校验结果如下：
```json
 {
   "exp": 1554388876,
   "user_name": "user",
   "authorities": [
     "USER"
   ],
   "client_id": "aaaaa",
   "scope": [
     "login",
     "userinfo"
   ]
 }

```
