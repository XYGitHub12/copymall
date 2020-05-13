package com.macro.copymall.search.controller;

import com.macro.copymall.search.domain.EsProduct;
import com.macro.copymall.common.api.CommonPage;
import com.macro.copymall.common.api.CommonResult;
import com.macro.copymall.search.service.EsProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 搜索商品管理Controller
 */
@Controller
@Api(tags = "EsProductController",description = "搜索商品管理")
@RequestMapping("/esProduct")
public class EsProductController {

    @Autowired
    private EsProductService productService;

    @ApiOperation(value = "导入所有数据库中商品到ES")
    @RequestMapping(value = "/importAll",method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<Integer> importAllList(){
        int count = productService.importAll();
        return CommonResult.success(count);
    }

    @ApiOperation(value = "根据id删除商品")
    @RequestMapping(value = "/delete/{id}",method = RequestMethod.GET)
    @ResponseBody
    public CommonResult delete(@PathVariable Long id){
        productService.delete(id);
        return CommonResult.success(null);
    }

    @ApiOperation(value = "根据id批量删除商品")
    @RequestMapping(value = "/delete/batch",method = RequestMethod.POST)
    @ResponseBody
    public CommonResult batch(@RequestParam("ids")List<Long> ids){
        productService.batch(ids);
        return CommonResult.success(null);
    }

    @ApiOperation(value = "根据id添加商品")
    @RequestMapping(value = "/create/{id}",method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<EsProduct> create(@PathVariable Long id){
        EsProduct product = productService.create(id);
        if (product!=null){
            return CommonResult.success(product);
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "简单搜索")
    @RequestMapping(value = "/search/simple",method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<EsProduct>> search(@RequestParam(required = false) String keyword,
                                                      @RequestParam(required = false,defaultValue = "0") Integer pageNum,
                                                      @RequestParam(required = false,defaultValue = "5") Integer pageSize){
        Page<EsProduct> esProductPage = productService.search(keyword,pageNum,pageSize);
        return CommonResult.success(CommonPage.restPage(esProductPage));
    }




}
