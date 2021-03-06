package com.gzdzss.gateway.web.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * @author <a href="mailto:zhouyanjie666666@gmail.com">zyj</a>
 * @date 2019/4/3
 */

@Component
public class AuthFilter implements GlobalFilter, Ordered {

    private static final String JWT_TO_ACCESS = "jwt_to_access:";

    private static final byte[] TOKEN_TIMEOUT_MSG= "token已过期".getBytes(StandardCharsets.UTF_8);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String token = headers.getFirst(HttpHeaders.AUTHORIZATION);
        if (token != null && token.toUpperCase().startsWith("Bearer ".toUpperCase())) {
            String accessToken = token.substring(7);
            //根据 accessToken 获取 jwtToken, 如果没有，则表示已经过期
            String jwtToken = stringRedisTemplate.opsForValue().get(JWT_TO_ACCESS + accessToken);
            if (jwtToken != null) {
                ServerHttpRequest host = exchange.getRequest().mutate().headers(httpHeaders -> {
                    httpHeaders.setBearerAuth(jwtToken);
                }).build();
                ServerWebExchange build = exchange.mutate().request(host).build();
                return chain.filter(build);
            } else {
                //过期提示:  statusCode: 401 ,  body:  token已过期
                ServerHttpResponse response = exchange.getResponse();
                DataBuffer buffer = response.bufferFactory().wrap(TOKEN_TIMEOUT_MSG);
                response.getHeaders().setContentType(MediaType.APPLICATION_JSON_UTF8);
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.writeWith(Mono.just(buffer));
            }
        }
        return chain.filter(exchange);
    }


    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}
