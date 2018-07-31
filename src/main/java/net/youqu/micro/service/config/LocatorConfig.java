package net.youqu.micro.service.config;

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

    /**
     * union服务路由
     *
     * @param builder
     * @return
     */
    @Bean
    @ConditionalOnBean(AuthFilter.class)
    public RouteLocator unionRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/union/**")
                        .filters(f -> f.filter(authFilter).stripPrefix(1))
                        .uri("lb://UNION")
                        .order(0)
                        .id("union")
                )
                .build();
    }

    /**
     * 内容服务路由
     *
     * @param builder
     * @return
     */
    @Bean
    @ConditionalOnBean(AuthFilter.class)
    public RouteLocator contentRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/content/**")
                        .filters(f -> f.filter(authFilter).stripPrefix(1))
                        .uri("lb://CONTENT")
                        .order(0)
                        .id("content")
                )
                .build();
    }

    /**
     * 认证服务路由
     *
     * @param builder
     * @return
     */
    @Bean
    public RouteLocator authRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/auth/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://AUTH")
                        .order(0)
                        .id("auth")
                )
                .build();
    }

    /**
     * 阅文服务
     *
     * @param builder
     * @return
     */
    @Bean
    public RouteLocator yuewenRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/ywservice/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://YWSERVICE")
                        .order(0)
                        .id("ywservice")
                )
                .build();
    }

    /**
     * 搜索服务
     *
     * @param builder
     * @return
     */
    @Bean
    public RouteLocator searchRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/dysearch/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://DYSEARCH")
                        .order(0)
                        .id("dysearch")
                )
                .build();
    }
}
