package com.macro.copymall.portal.controller;

import com.macro.copymall.common.api.CommonResult;
import com.macro.copymall.mbg.model.OmsCartItem;
import com.macro.copymall.portal.model.CartProduct;
import com.macro.copymall.portal.model.CartPromotionItem;
import com.macro.copymall.portal.service.OmsCartItemService;
import com.macro.copymall.portal.service.UmsMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(tags = "OmsCartItemController",description = "购物车管理")
@RequestMapping("/cart")
public class OmsCartItemController {

    @Autowired
    private OmsCartItemService cartItemService;
    @Autowired
    private UmsMemberService memberService;

    @ApiOperation("添加商品到购物车")
    @PostMapping("/add")
    public CommonResult add(@RequestBody OmsCartItem cartItem){
        int count = cartItemService.add(cartItem);
        if (count > 0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("获取会员的购物车列表")
    @GetMapping("/list")
    public CommonResult<List<OmsCartItem>> list(){
        List<OmsCartItem> cartItemList = cartItemService.list(memberService.getCurrentMember().getId());
        return CommonResult.success(cartItemList);
    }

    @ApiOperation("获取包含促销信息的购物车列表")
    @GetMapping("/list/promotion")
    public CommonResult<List<CartPromotionItem>> listPromotion(){
        List<CartPromotionItem> cartPromotionItemList = cartItemService.listPromotion(memberService.getCurrentMember().getId());
        return CommonResult.success(cartPromotionItemList);
    }

    @ApiOperation("修改购物车商品的数量")
    @PostMapping("/update/quantity")
    public CommonResult updateQuantity(@RequestParam Long id,
                                       @RequestParam Integer quantity){
        int count = cartItemService.updateQuantity(id,memberService.getCurrentMember().getId(),quantity);
        if (count > 0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("获取购物车中某个商品的规格,用于重选规格")
    @GetMapping("/getProduct/{productId}")
    public CommonResult<CartProduct> getCartProduct(@PathVariable Long productId){
        CartProduct cartProduct = cartItemService.getCartProduct(productId);
        return CommonResult.success(cartProduct);
    }

    @ApiOperation("修改购物车中商品的规格")
    @PostMapping("/update/attr")
    public CommonResult updateAttr(@RequestBody OmsCartItem cartItem){
        int count = cartItemService.updateAttr(cartItem);
        if (count > 0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("删除购物车中的某个商品")
    @PostMapping("/delete")
    public CommonResult delete(@RequestParam("ids") List<Long> ids){
        int count = cartItemService.delete(memberService.getCurrentMember().getId(),ids);
        if (count > 0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("清空购物车")
    @PostMapping("/clear")
    public CommonResult clear(){
        int count = cartItemService.clear(memberService.getCurrentMember().getId());
        if (count > 0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

}
