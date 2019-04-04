package com.gzdzss.gateway.web.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author <a href="mailto:zhouyanjie666666@gmail">zyj</a>
 * @date 2019/4/3
 */

@Component
public class AuthFilter implements GlobalFilter, Ordered {

    private static final String JWT_TO_ACCESS = "jwt_to_access:";

    //是否拦截  accessToken 不在redis中的请求
    private static final boolean INTERCEPT_BEARER = false;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        HttpHeaders headers = exchange.getRequest().getHeaders();
        String token = headers.getFirst(HttpHeaders.AUTHORIZATION);
        if (token != null && token.startsWith("Bearer ")) {
            String accessToken = token.substring(7);
            String jwtToken = stringRedisTemplate.opsForValue().get(JWT_TO_ACCESS + accessToken);
            if (jwtToken != null) {
                ServerHttpRequest host = exchange.getRequest().mutate().headers(httpHeaders -> {
                    httpHeaders.setBearerAuth(jwtToken);
                }).build();
                ServerWebExchange build = exchange.mutate().request(host).build();
                return chain.filter(build);
            }

            if (INTERCEPT_BEARER) {
                throw new RuntimeException("token已过期");
            }

        }
        return chain.filter(exchange);
    }


    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}
