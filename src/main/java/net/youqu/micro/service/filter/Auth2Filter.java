package net.youqu.micro.service.filter;

import com.alibaba.fastjson.JSONObject;
import net.youqu.micro.service.enums.ResultCodeEnum;
import net.youqu.micro.service.model.Result;
import net.youqu.micro.service.utils.RedisUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.TreeMap;

/**
 * description:网关鉴权
 *
 * @author wangpeng
 * @date 2018/06/06
 */
@Component
public class Auth2Filter implements GatewayFilter, Ordered {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private RedisUtil redisUtil;

    @Value("${spring.profiles.active}")
    private String profile;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        exchange.getResponse().setStatusCode(HttpStatus.OK);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        Result result = new Result();
        //后端调用跳过验签
        boolean skipAuth = Boolean.valueOf(exchange.getRequest().getQueryParams().getFirst("skipAuth"));
        if (!"dev".equals(profile)&&!"pre".equals(profile)) {
            skipAuth = false;
        }
        if (!skipAuth) {
            String sign = exchange.getRequest().getQueryParams().getFirst("sign");
            if (StringUtils.isEmpty(sign)) {
                //没有验签参数
                result.setCode(ResultCodeEnum.SGIN_EMPTY.getCode());
                result.setMsg(ResultCodeEnum.SGIN_EMPTY.getMsg());
                return exchange.getResponse().writeWith(Flux.just(this.getBodyBuffer(exchange.getResponse(), result)));
            }
            String publicKey = exchange.getRequest().getHeaders().getFirst("publicKey");
            if (StringUtils.isBlank(publicKey)) {
                publicKey = exchange.getRequest().getQueryParams().getFirst("p");
                if (StringUtils.isBlank(publicKey)) {
                    //没有公钥
                    result.setCode(ResultCodeEnum.PUBLICKEY_EMPTY.getCode());
                    result.setMsg(ResultCodeEnum.PUBLICKEY_EMPTY.getMsg());
                    return exchange.getResponse().writeWith(Flux.just(this.getBodyBuffer(exchange.getResponse(), result)));
                }
            }
            String privateKey = redisUtil.getValue(publicKey);
            if (!StringUtils.isEmpty(privateKey)) {
                TreeMap<String, List<String>> parameterMap = new TreeMap<>(exchange.getRequest().getQueryParams());
                //验签
                StringBuilder sb = new StringBuilder();
                parameterMap.entrySet().forEach(stringEntry -> {
                    if (!StringUtils.equalsIgnoreCase(stringEntry.getKey(), "sign") && !StringUtils.equalsIgnoreCase(stringEntry.getKey(), "p")) {
                        if (!CollectionUtils.isEmpty(stringEntry.getValue())) {
                            sb.append(stringEntry.getKey());
                            sb.append("=");
                            sb.append(stringEntry.getValue().stream().findFirst().get());
                        }
                    }
                });
                sb.append("privateKey=");
                sb.append(privateKey);
                String urlString = sb.toString();
                try {
                    urlString = URLEncoder.encode(sb.toString(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    logger.error("url encode fail");
                }
                logger.info(urlString);
                String serverSign = DigestUtils.md5Hex(urlString);
                logger.info(serverSign);
                if (!serverSign.equals(sign)) {
                    //验签不通过
                    result.setCode(ResultCodeEnum.SIGN_INVALID.getCode());
                    result.setMsg(ResultCodeEnum.SIGN_INVALID.getMsg());
                    return exchange.getResponse().writeWith(Flux.just(this.getBodyBuffer(exchange.getResponse(), result)));
                }
            } else {
                //私钥过期
                result.setCode(ResultCodeEnum.PRIVATEKEY_EXPIRE.getCode());
                result.setMsg(ResultCodeEnum.PRIVATEKEY_EXPIRE.getMsg());
                return exchange.getResponse().writeWith(Flux.just(this.getBodyBuffer(exchange.getResponse(), result)));
            }
        }
        return chain.filter(exchange);
    }

    /**
     * 封装返回值
     *
     * @param response
     * @param result
     * @return
     */
    private DataBuffer getBodyBuffer(ServerHttpResponse response, Result result) {
        return response.bufferFactory().wrap(JSONObject.toJSONBytes(result));
    }

    @Override
    public int getOrder() {
        return -200;
    }
}
