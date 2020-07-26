package com.macro.copymall.portal.controller;

import com.macro.copymall.common.api.CommonResult;
import com.macro.copymall.mbg.model.UmsMember;
import com.macro.copymall.portal.service.UmsMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;

/**
 * 会员管理Controller（获取验证码、注册、登录、获取会员信息、修改密码、刷新token）
 * 问题tokenHeader和tokenHead
 */
@RestController
@RequestMapping("/sso")
@Api(tags = "UmsMemberController",description = "会员管理")
public class UmsMemberController {

    @Autowired
    private UmsMemberService memberService;

    //注入jwt存储的请求头
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    //注入jwt负载的头
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @ApiOperation("获取验证码")
    @PostMapping("/getAuthCode")
    public CommonResult getAuthCode(@RequestParam String telephone){
        String authCode = memberService.getAuthCode(telephone);
        return CommonResult.success(authCode,"验证码获取成功");
    }

    @ApiOperation("会员注册")
    @PostMapping("/register")
    public CommonResult register(@RequestParam String username,
                                 @RequestParam String password,
                                 @RequestParam String telephone,
                                 @RequestParam String authCode){
        memberService.register(username,password,telephone,authCode);
        return CommonResult.success(null,"注册成功");
    }

    @ApiOperation("会员登录")
    @PostMapping("/login")
    public CommonResult login(@RequestParam String username,
                              @RequestParam String password){
        String token = memberService.login(username, password);
        if (token == null){
            return CommonResult.failed("用户名或密码错误");
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("token",token);
        map.put("tokenHead",tokenHead);
        return CommonResult.success(map);
    }

    @ApiOperation("获取会员信息")
    @GetMapping("/info")
    public CommonResult info(Principal principal){
        if (principal == null){
            return CommonResult.unauthorized(null);
        }
        UmsMember currentMember = memberService.getCurrentMember();
        return CommonResult.success(currentMember);
    }

    @ApiOperation("修改密码")
    @PostMapping("/updatePassword")
    public CommonResult updatePassword(@RequestParam String telephone,
                                       @RequestParam String password,
                                       @RequestParam String authCode){
        memberService.updatePassword(telephone,password,authCode);
        return CommonResult.success(null,"密码修改成功");
    }

    @ApiOperation("刷新token")
    @GetMapping("/refreshToken")
    public CommonResult refreshToken(HttpServletRequest request){
        String token = request.getHeader(tokenHeader);
        String refreshToken = memberService.refreshToken(token);
        if (refreshToken == null){
            return CommonResult.failed("token已经过期");
        }
        HashMap<String,String> map = new HashMap<>();
        map.put("token",refreshToken);
        map.put("tokenHead",tokenHead);
        return CommonResult.success(map);
    }

}
