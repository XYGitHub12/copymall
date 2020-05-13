package com.macro.copymall.admin.controller;

import com.macro.copymall.common.api.CommonPage;
import com.macro.copymall.common.api.CommonResult;
import com.macro.copymall.admin.dto.PmsProductAttributeParam;
import com.macro.copymall.admin.dto.ProductAttrInfo;
import com.macro.copymall.mbg.model.PmsProductAttribute;
import com.macro.copymall.admin.service.PmsProductAttributeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品属性管理Controller
 */
@Controller
@Api(tags = "PmsProductAttributeController",description = "商品属性管理")
@RequestMapping("/productAttribute")
public class PmsProductAttributeController {

    @Autowired
    private PmsProductAttributeService productAttributeService;

    @ApiOperation("根据分类查询属性列表或参数列表")
    @ApiImplicitParams({@ApiImplicitParam(name = "type", value = "0表示属性，1表示参数", required = true, paramType = "query", dataType = "integer")})
    @RequestMapping(value = "/list/{cid}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<PmsProductAttribute>> getList(@PathVariable("cid") Long cid,
                                                                 @RequestParam(value = "type") Integer type,
                                                                 @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                                                 @RequestParam(value = "pageSize",defaultValue = "5") Integer pageSize){
        List<PmsProductAttribute> list = productAttributeService.getList(cid,type,pageNum,pageSize);
        return CommonResult.success(CommonPage.restPage(list));
    }

    @ApiOperation("添加商品属性信息")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult create(@RequestBody PmsProductAttributeParam pmsProductAttributeParam, BindingResult bindingResult){
        int count = productAttributeService.create(pmsProductAttributeParam);
        if (count>0){
            return CommonResult.success(count);
        }else {
            return CommonResult.failed();
        }
    }

    @ApiOperation("修改商品属性信息")
    @RequestMapping(value = "/update/{id}",method = RequestMethod.POST)
    @ResponseBody
    public CommonResult update(@PathVariable Long id,@RequestBody PmsProductAttributeParam pmsProductAttributeParam,BindingResult bindingResult){
        int count = productAttributeService.update(id,pmsProductAttributeParam);
        if (count>0){
            return CommonResult.success(count);
        }else {
            return CommonResult.failed();
        }
    }

    @ApiOperation("查询单个商品属性")
    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<PmsProductAttribute> getItem(@PathVariable Long id){
        PmsProductAttribute pmsProductAttribute = productAttributeService.getItem(id);
        return CommonResult.success(pmsProductAttribute);
    }

    @ApiOperation("批量删除商品属性")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(@RequestParam("ids") List<Long> ids) {
        int count = productAttributeService.delete(ids);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @ApiOperation("根据商品分类的id获取商品属性及属性分类")
    @RequestMapping(value = "/attrInfo/{productCategoryId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<ProductAttrInfo>> getAttrInfo(@PathVariable Long productCategoryId) {
        List<ProductAttrInfo> productAttrInfoList = productAttributeService.getProductAttrInfo(productCategoryId);
        return CommonResult.success(productAttrInfoList);
    }

}





























