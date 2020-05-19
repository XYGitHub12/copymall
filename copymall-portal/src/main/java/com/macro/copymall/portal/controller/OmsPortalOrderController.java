package com.macro.copymall.portal.controller;

import com.macro.copymall.portal.service.OmsPortalOrderService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 订单管理Controller
 */
@Controller
@Api(tags = "OmsPortalOrderController", description = "订单管理")
@RequestMapping("/order")
public class OmsPortalOrderController {

    @Autowired
    private OmsPortalOrderService omsPortalOrderService;

//    @ApiOperation("根据购物车信息生成确认单信息")
//    @RequestMapping(value = "/generateConfirmOrder", method = RequestMethod.POST)
//    @ResponseBody
//    public CommonResult<ConfirmOrderResult> generateConfirmOrder() {
//        ConfirmOrderResult confirmOrderResult = portalOrderService.generateConfirmOrder();
//        return CommonResult.success(confirmOrderResult);
//    }
//
//    @ApiOperation("根据购物车信息生成订单")
//    @RequestMapping(value = "/generateOrder", method = RequestMethod.POST)
//    @ResponseBody
//    public CommonResult generateOrder(@RequestBody OrderParam orderParam) {
//        Map<String, Object> result = portalOrderService.generateOrder(orderParam);
//        return CommonResult.success(result, "下单成功");
//    }
//
//    @ApiOperation("支付成功的回调")
//    @RequestMapping(value = "/paySuccess", method = RequestMethod.POST)
//    @ResponseBody
//    public CommonResult paySuccess(@RequestParam Long orderId) {
//        Integer count = portalOrderService.paySuccess(orderId);
//        return CommonResult.success(count, "支付成功");
//    }
//
//    @ApiOperation("自动取消超时订单")
//    @RequestMapping(value = "/cancelTimeOutOrder", method = RequestMethod.POST)
//    @ResponseBody
//    public CommonResult cancelTimeOutOrder() {
//        portalOrderService.cancelTimeOutOrder();
//        return CommonResult.success(null);
//    }
//
//    @ApiOperation("取消单个超时订单")
//    @RequestMapping(value = "/cancelOrder", method = RequestMethod.POST)
//    @ResponseBody
//    public CommonResult cancelOrder(Long orderId) {
//        portalOrderService.sendDelayMessageCancelOrder(orderId);
//        return CommonResult.success(null);
//    }

}
