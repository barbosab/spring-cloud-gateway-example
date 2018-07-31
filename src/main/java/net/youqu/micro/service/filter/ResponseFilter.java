package net.youqu.micro.service.filter;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.ValueFilter;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * description:网关响应过滤器
 *
 * @author wangpeng
 * @date 2018/06/06
 */
@Component
public class ResponseFilter implements GlobalFilter, Ordered {
    @Value("${spring.profiles.active}")
    private String profile;

//    private final static Logger loggerKafka = LoggerFactory.getLogger(net.youqu.kafka.log.appender.KafkaLogAppender.class);

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, GatewayFilterChain gatewayFilterChain) {
        serverWebExchange.getAttributes().put("start_time", System.currentTimeMillis());
        return gatewayFilterChain.filter(serverWebExchange).then(
                Mono.fromRunnable(() -> {
                    this.logCollect(serverWebExchange);
                })
        );
    }

    /**
     * 日志收集
     *
     * @param serverWebExchange
     */
    private void logCollect(ServerWebExchange serverWebExchange) {
        ServerHttpRequest request = serverWebExchange.getRequest();
        ServerHttpResponse response = serverWebExchange.getResponse();
        CompletableFuture.runAsync(() -> {
            long startTime = Long.valueOf(String.valueOf(serverWebExchange.getAttributes().get("start_time")));
            long consumeTime = System.currentTimeMillis() - startTime;
            JSONObject data = new JSONObject() {{
                //kafka参数
                put("topic", "cp_linked_up");
                put("table_name", "cp_linked_up");
                //网关层参数
                put("request_id", UUID.randomUUID().toString());
                put("uri", request.getURI().getPath());
                if (!StringUtils.isEmpty(request.getURI().getQuery())) {
                    put("request_uri", request.getURI().getPath() + "?" + request.getURI().getQuery());
                } else {
                    put("request_uri", request.getURI().getPath());
                }
                put("request_method", request.getMethodValue());
                put("request_host", request.getRemoteAddress().getHostString());
                put("request_remote_addr", request.getRemoteAddress().getAddress().getHostAddress());
                put("request_remote_user", request.getURI().getRawUserInfo());
                put("request_protocol", request.getURI().getScheme());
                put("request_status", response.getStatusCode().value());
                put("request_time", consumeTime);
                put("http_referer", request.getHeaders().getFirst("referer"));
                put("body_bytes_sent", "");
                put("http_user_agent", request.getHeaders().getFirst("user-agent"));
                put("start_time", startTime);
                put("log_time", System.currentTimeMillis());
                //第三方参数
                put("external_type", response.getHeaders().getFirst("external_type"));
                put("external_uri", response.getHeaders().getFirst("external_uri"));
                put("external_request_uri", response.getHeaders().getFirst("external_request_uri"));
                put("external_request_time", response.getHeaders().getFirst("external_request_time"));
                put("external_code", response.getHeaders().getFirst("external_code"));
                put("external_response", response.getHeaders().getFirst("external_response"));
                //业务参数
                put("packageName", request.getQueryParams().getFirst("packageName"));
                put("udid", request.getQueryParams().getFirst("udid"));
                put("channelId", request.getQueryParams().getFirst("channelId"));
                put("os", request.getQueryParams().getFirst("os"));
                put("version", request.getQueryParams().getFirst("version"));
                put("longitude", request.getQueryParams().getFirst("longitude"));
                put("latitude", request.getQueryParams().getFirst("latitude"));
                put("cityCode", request.getQueryParams().getFirst("cityCode"));
                put("bookChapterId", request.getQueryParams().getFirst("book_chapter_id"));
                put("chapterId", request.getQueryParams().getFirst("chapter_id"));
                put("bookSourceId", request.getQueryParams().getFirst("book_source_id"));
                put("bookId", request.getQueryParams().getFirst("book_id"));
            }};
            if (StringUtils.equalsIgnoreCase("prod", profile)) {
//                loggerKafka.info(JSON.toJSONString(data, filter));
            }
        });
    }


    private ValueFilter filter = (obj, s, v) -> {
        if (v == null) {
            return "";
        }
        return v;
    };

    @Override
    public int getOrder() {
        return 0;
    }
}
