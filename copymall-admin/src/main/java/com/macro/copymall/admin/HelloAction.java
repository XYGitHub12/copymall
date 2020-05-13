package com.macro.copymall.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试骨架运行
 */
@RestController
public class HelloAction {

    @GetMapping("/hello")
    public String hello(){
        return "hello===========";
    }
}
