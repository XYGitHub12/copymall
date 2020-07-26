//package com.macro.copymall.admin.controller;
//
//import com.macro.copymall.common.api.CommonPage;
//import com.macro.copymall.common.api.CommonResult;
//import com.macro.copymall.admin.model.PmsBrandParam;
//import com.macro.copymall.mbg.model.PmsBrand;
//import com.macro.copymall.admin.service.PmsBrandService;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.validation.BindingResult;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
///**
// * 商品品牌功能Controller
// */
//@Controller
//@Api(tags = "PmsBrandController",description = "商品品牌管理")
//@RequestMapping("/brand")
//public class PmsBrandController {
//
//    @Autowired
//    private PmsBrandService brandService;
//
//    @ApiOperation(value = "获取全部品牌列表")
//    @RequestMapping(value = "/listAll",method = RequestMethod.GET)
//    @ResponseBody
//    public CommonResult<List<PmsBrand>> getList(){
//        return CommonResult.success(brandService.listAllBrand());
//    }
//
//    @ApiOperation(value = "添加品牌")
//    @RequestMapping(value = "/create",method = RequestMethod.POST)
//    @ResponseBody
//    public CommonResult create(@Validated @RequestBody PmsBrandParam pmsBrandParam, BindingResult result){
//        //在使用@Validated进行参数校验的时候可以用BindingResult对象将所有的异常信息存起来
//        CommonResult commonResult;
//        int count = brandService.createBrand(pmsBrandParam);
//        if(count == 1){
//            commonResult = CommonResult.success(count);
//        }else {
//            commonResult = CommonResult.failed();
//        }
//        return commonResult;
//    }
//
//    @ApiOperation(value = "更新品牌")
//    @RequestMapping(value = "/update/{id}",method = RequestMethod.POST)
//    @ResponseBody
//    public CommonResult update(@PathVariable("id") Long id,
//                               @Validated @RequestBody PmsBrandParam pmsBrandParam,
//                               BindingResult result){
//        int count = brandService.updateBrand(id,pmsBrandParam);
//        CommonResult commonResult;
//        if (count == 1){
//            commonResult = CommonResult.success(count);
//        }else {
//            commonResult = CommonResult.failed();
//        }
//        return commonResult;
//    }
//
//    @ApiOperation(value = "删除品牌")
//    @RequestMapping(value = "/delete/{id}}",method = RequestMethod.GET)
//    @ResponseBody
//    public CommonResult delete(@PathVariable("id") Long id){
//        int count = brandService.deleteBrand(id);
//        CommonResult commonResult;
//        if (count == 1){
//            commonResult = CommonResult.success(count);
//        }else {
//            commonResult = CommonResult.failed();
//        }
//        return commonResult;
//    }
//
//    @ApiOperation(value = "根据品牌名称分页获取品牌列表")
//    @RequestMapping(value = "/list",method = RequestMethod.GET)
//    @ResponseBody
//    public CommonResult<CommonPage<PmsBrand>> getList(@RequestParam(value = "keyword",required = false) String keyword,
//                                                      @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
//                                                      @RequestParam(value = "pageSize",defaultValue = "5") Integer pageSize){
//        List<PmsBrand> listBrand = brandService.listBrand(keyword,pageNum,pageSize);
//        return CommonResult.success(CommonPage.restPage(listBrand));
//    }
//
//    @ApiOperation(value = "根据编号查询品牌信息")
//    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
//    @ResponseBody
//    public CommonResult<PmsBrand> getItem(@PathVariable("id") Long id){
//        return CommonResult.success(brandService.getBrand(id));
//    }
//
//    @ApiOperation(value = "批量删除品牌")
//    @RequestMapping(value = "/delete/batch",method = RequestMethod.POST)
//    @ResponseBody
//    public CommonResult deleteBatch(@RequestParam("ids") List<Long> ids){
//        int count = brandService.deleteBrand(ids);
//        if (count>0){
//            return CommonResult.success(count);
//        }else {
//            return CommonResult.failed();
//        }
//    }
//
//    @ApiOperation(value = "批量更新显示状态")
//    @RequestMapping(value = "/update/showStatus",method = RequestMethod.POST)
//    @ResponseBody
//    public CommonResult updateShowStatus(@RequestParam("ids") List<Long> ids,
//                                         @RequestParam("showStatus") Integer showStatus){
//        int count = brandService.updateShowStatus(ids,showStatus);
//        if (count>0){
//            return CommonResult.success(count);
//        }else {
//            return CommonResult.failed();
//        }
//    }
//
//    @ApiOperation(value = "批量更新厂家制造商状态")
//    @RequestMapping(value = "/update/factoryStatus",method = RequestMethod.POST)
//    @ResponseBody
//    public CommonResult updateFactoryStatus(@RequestParam("ids") List<Long> ids,
//                                            @RequestParam("factoryStatus") Integer factoryStatus){
//        int count = brandService.updateFactoryStatus(ids,factoryStatus);
//        if (count>0){
//            return CommonResult.success(count);
//        }else {
//            return CommonResult.failed();
//        }
//    }
//
//}
