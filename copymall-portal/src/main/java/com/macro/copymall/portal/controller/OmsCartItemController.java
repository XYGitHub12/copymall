package com.macro.copymall.portal.controller;

import com.macro.copymall.common.api.CommonResult;
import com.macro.copymall.mbg.model.OmsCartItem;
import com.macro.copymall.portal.service.OmsCartItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 购物车管理Controller
 */
@Controller
@Api(tags = "OmsCartItemController",description = "购物车管理")
@RequestMapping("/cart")
public class OmsCartItemController {

    @Autowired
    private OmsCartItemService omsCartItemService;

    @ApiOperation("添加商品到购物车")
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ResponseBody
    public CommonResult add(@RequestBody OmsCartItem omsCartItem){
        int count = omsCartItemService.add(omsCartItem);
        if (count>0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

}
