package com.macro.copymall.portal.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Api(tags = "UserAction",description = "后2222222222222222222222222")
@RequestMapping("/sso")
public class UserAction {

    @ApiOperation("测试")
    @RequestMapping(value = "/test",method = RequestMethod.GET)
    @ResponseBody
    public String getIndex(){
        return "233333333333333333";
    }

}
