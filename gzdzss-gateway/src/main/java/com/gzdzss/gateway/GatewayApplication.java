package com.gzdzss.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * @author <a href="mailto:zhouyanjie666666@gmail.com">zyj</a>
 * @date 2019/4/3
 */

@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

    // webflux
    @Bean
    public RouterFunction<ServerResponse> index() {
        return route(GET("/"), request -> {
            String hostAddress = request.exchange().getRequest().getRemoteAddress().getAddress().getHostAddress();
            return ServerResponse.ok().body(Mono.just(hostAddress), String.class);
        });
    }

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
