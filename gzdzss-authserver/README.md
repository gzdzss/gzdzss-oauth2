#认证中心


## 1.环境
### 1.1 redis

docker run --name redis  -p 6379:6379 -d  redis:5  --requirepass "gzdzssredispassword" 


### 1.2 mysql
docker run --name mysql -v  data:/var/lib/mysql   -p 3306:3306  -e MYSQL_ROOT_PASSWORD=gzdzss -d mysql:5.7





```sql
##创建数据库
CREATE DATABASE authserver CHARACTER SET utf8mb4;

use authserver;

CREATE TABLE `gzdzss_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(20) DEFAULT NULL,
  `password` varchar(256) DEFAULT NULL,
  `enabled` tinyint(1) NOT NULL,
  `avatar_url` varchar(255) DEFAULT NULL COMMENT '头像地址',
  `nick_name` varchar(255) DEFAULT NULL COMMENT '昵称',
  `github_id` varchar(255) DEFAULT NULL,
  `github_token` varchar(255) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `modified_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_github_id` (`github_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;



CREATE TABLE `gzdzss_authorities` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `authority` varchar(255) NOT NULL,
  `modified_date` datetime DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

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
  `scope` varchar(256) DEFAULT NULL COMMENT '可用范围 逗号分隔',
  `authorized_grant_types` varchar(256) DEFAULT NULL,
  `web_server_redirect_uri` varchar(256) DEFAULT NULL,
  `authorities` varchar(256) DEFAULT NULL,
  `access_token_validity` int(11) DEFAULT NULL COMMENT '默认有效期,默认:43200',
  `refresh_token_validity` int(11) DEFAULT NULL COMMENT '默认刷新有效期:2592000',
  `additional_information` varchar(4096) DEFAULT NULL,
  `autoapprove` varchar(256) DEFAULT NULL COMMENT '自动审核的范围， 逗号分隔',
  `username` varchar(20) NOT NULL,
  PRIMARY KEY (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;




```


### 1.3注册用户：  （或者通过前端页面进行操作  gzdzss-auth-web）
POST: http://localhost:8888/auth/user/register   
  
  例： username:user
       password:123456

返回 statueCode = 200 表示成功        


### 1.4登录
POST: http://localhost:8888/auth/login
 例：   username:user
       password:123456
返回：
{
    "access_token": "fc77ede1-8baa-4df3-b3d6-f340f11e5b8c",
    "token_type": "bearer",
    "expires_in": 43200
}

jwtToken 需要从redis里面查找 （或者直接通过网关调用直接使用 access_token）

例如：
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhY2Nlc3NfdG9rZW4iOiJmYzc3ZWRlMS04YmFhLTRkZjMtYjNkNi1mMzQwZjExZTViOGMiLCJwcmluY2lwYWwiOiJ7XCJhY2NvdW50Tm9uRXhwaXJlZFwiOnRydWUsXCJhY2NvdW50Tm9uTG9ja2VkXCI6dHJ1ZSxcImNyZWRlbnRpYWxzTm9uRXhwaXJlZFwiOnRydWUsXCJlbmFibGVkXCI6dHJ1ZSxcImlkXCI6NixcInVzZXJuYW1lXCI6XCJ1c2VyXCJ9IiwiYXV0aG9yaXRpZXMiOiJVU0VSIn0.9JIUWlrDrAVOs1YhuTw_0--7l6tUC23GBNuLCmWP5yM



### 1.5.注册客户端 (设置Headers:  Authorization = Bearer eyJhbGciOiJIUzI1NiIsInR5c……)
POST：http://localhost:8888/auth/client/register  
例如;  
  clientId:aaaaa
  clientSecret: bbbbb
  callbackUrl: http://localhost:8081/gzdzss/oauth2

### 1.6.查看客户端  (设置Headers:  Authorization = Bearer eyJhbGciOiJIUzI1NiIsInR5c……)
GET: http://localhost:8888/auth/client/list
GET: http://localhost:8888/auth/client/detail/{client_id}

