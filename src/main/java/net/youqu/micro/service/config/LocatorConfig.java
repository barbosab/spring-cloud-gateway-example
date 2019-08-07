package net.youqu.micro.service.config;

import net.youqu.micro.service.filter.AddParamFilter;
import net.youqu.micro.service.filter.Auth2Filter;
import net.youqu.micro.service.filter.AuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description:
 *
 * @author wangpeng
 * @date 2018/06/07
 */
@Configuration
public class LocatorConfig {
    @Autowired
    private AuthFilter authFilter;


    @Bean
    @ConditionalOnBean(AuthFilter.class)
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/auth/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://AUTH")
                        .order(0)
                        .id("auth")
                )
                .route(r -> r.path("/union/**")
                        .filters(f -> f.filters(authFilter).stripPrefix(1))
                        .uri("lb://UNION")
                        .order(0)
                        .id("union")
                )
                .route(r -> r.path("/content/**")
                        .filters(f -> f.filter(authFilter).stripPrefix(1))
                        .uri("lb://CONTENT")
                        .order(0)
                        .id("content")
                )
                .route(r -> r.path("/RECOMMENDNEW/**")
                        .filters(f -> f.filters(authFilter).stripPrefix(1))
                        .uri("lb://RECOMMEND")
                        .order(0)
                        .id("recommend")
                )
                .build();
    }
}
