package com.macro.copymall.search.controller;

import com.macro.copymall.search.domain.EsProduct;
import com.macro.copymall.common.api.CommonPage;
import com.macro.copymall.common.api.CommonResult;
import com.macro.copymall.search.service.EsProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品搜索管理Controller
 */
@Controller
@Api(tags = "EsProductController",description = "搜索商品管理")
@RequestMapping("/esProduct")
public class EsProductController {

    @Autowired
    private EsProductService productService;

    @ApiOperation(value = "导入数据库中所有商品到ES")
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

    @ApiOperation("综合搜索、筛选、排序")
    @ApiImplicitParam(name = "sort",value = "排序字段:0->按相关度；1->按新品；2->按销量；3->价格从低到高；4->价格从高到低",
            defaultValue = "0",allowableValues = "0,1,2,3,4",paramType = "query",dataType = "integer")
    @RequestMapping(value = "/search",method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<CommonPage<EsProduct>> search(@RequestParam(required = false) String keyword,
                                                      @RequestParam(required = false) Long brandId,
                                                      @RequestParam(required = false) Long productCategoryId,
                                                      @RequestParam(required = false,defaultValue = "0") Integer pageNum,
                                                      @RequestParam(required = false,defaultValue = "5") Integer pageSize,
                                                      @RequestParam(required = false,defaultValue = "0") Integer sort){
        Page<EsProduct> esProductPage = productService.search(keyword,brandId,productCategoryId,pageNum,pageSize,sort);
        return CommonResult.success(CommonPage.restPage(esProductPage));
    }

}
