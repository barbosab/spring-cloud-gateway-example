package net.youqu.micro.service.filter;

import net.youqu.micro.service.utils.IpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import javax.xml.stream.events.Characters;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;

/**
 * description:
 *
 * @author wangpeng
 * @date 2018/10/18
 */
@Component
public class AddParamFilter implements GlobalFilter, Ordered {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        URI uri = exchange.getRequest().getURI();
        StringBuilder query = new StringBuilder();
        String originalQuery = uri.getRawQuery();

        if (StringUtils.hasText(originalQuery)) {
            query.append(originalQuery);
            if (originalQuery.charAt(originalQuery.length() - 1) != '&') {
                query.append('&');
            }
        }
        String remoteHost = IpUtil.getRemoteHost(exchange.getRequest());
        if (org.apache.commons.lang3.StringUtils.containsAny(remoteHost, ",")) {
            remoteHost = org.apache.commons.lang3.StringUtils.substring(remoteHost, 0, remoteHost.indexOf(","));
        }
        //TODO urlencode?
        query.append("ip");
        query.append('=');
        query.append(remoteHost);
        try {
            URI newUri = UriComponentsBuilder.fromUri(uri)
                    .replaceQuery(query.toString())
                    .build(true)
                    .toUri();

            ServerHttpRequest request = exchange.getRequest().mutate().uri(newUri).build();

            return chain.filter(exchange.mutate().request(request).build());
        } catch (RuntimeException ex) {
            logger.error("Invalid URI query:{}", query.toString());
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
