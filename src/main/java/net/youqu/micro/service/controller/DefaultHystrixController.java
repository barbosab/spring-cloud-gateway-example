package net.youqu.micro.service.controller;

import net.youqu.micro.service.enums.ResultCodeEnum;
import net.youqu.micro.service.model.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 默认降级处理
 *
 * @author huangting
 * @date 2019/07/29
 */
@RestController
public class DefaultHystrixController {

    @RequestMapping("/defaultfallback")
    public Result defaultfallback() {
        Result result = new Result();
        result.setCode(ResultCodeEnum.TIMEOUT.getCode());
        result.setMsg(ResultCodeEnum.TIMEOUT.getMsg());
        result.setData(null);
        return result;
    }
}