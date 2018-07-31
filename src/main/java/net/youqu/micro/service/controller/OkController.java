package net.youqu.micro.service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * description:
 *
 * @author wangpeng
 * @date 2018/06/13
 */
@RestController
public class OkController {
    @RequestMapping("ok")
    public String ok() {
        return "ok";
    }
}
