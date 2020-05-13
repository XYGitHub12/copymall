package com.macro.copymall.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.copymall.admin.dto.PmsBrandParam;
import com.macro.copymall.mbg.mapper.PmsBrandMapper;
import com.macro.copymall.mbg.mapper.PmsProductMapper;
import com.macro.copymall.mbg.model.PmsBrand;
import com.macro.copymall.mbg.model.PmsBrandExample;
import com.macro.copymall.mbg.model.PmsProduct;
import com.macro.copymall.mbg.model.PmsProductExample;
import com.macro.copymall.admin.service.PmsBrandService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 商品品牌Service实现类
 */
@Service
public class PmsBrandServiceImpl implements PmsBrandService {

    @Autowired
    private PmsBrandMapper brandMapper;
    @Autowired
    private PmsProductMapper productMapper;

    @Override
    public List<PmsBrand> listAllBrand() {
        PmsBrandExample example = new PmsBrandExample();
        List<PmsBrand> result = brandMapper.selectByExample(example);
        System.out.println(result);
        return result;
    }

    @Override
    public int createBrand(PmsBrandParam pmsBrandParam) {
        PmsBrand pmsBrand = new PmsBrand();
        BeanUtils.copyProperties(pmsBrandParam,pmsBrand);

        //如果创建的时候首字母为空，去名称的第一个为首字母
        if (StringUtils.isEmpty(pmsBrand.getFirstLetter())){
            pmsBrand.setFirstLetter(pmsBrand.getName().substring(0,1));
        }
        return brandMapper.insertSelective(pmsBrand);
    }

    @Override
    public int updateBrand(Long id, PmsBrandParam pmsBrandParam) {
        PmsBrand pmsBrand = new PmsBrand();
        BeanUtils.copyProperties(pmsBrandParam,pmsBrand);
        pmsBrand.setId(id);
        //如果创建的时候首字母为空，取名称的第一个为首字母
        if (StringUtils.isEmpty(pmsBrand.getFirstLetter())){
            pmsBrand.setFirstLetter(pmsBrand.getName().substring(0,1));
        }
        //更新品牌时更新商品中品牌的名称
        PmsProduct pmsProduct = new PmsProduct();
        pmsProduct.setBrandName(pmsBrand.getName());
        PmsProductExample pmsProductExample = new PmsProductExample();
        pmsProductExample.createCriteria().andBrandIdEqualTo(id);
        productMapper.updateByExampleSelective(pmsProduct,pmsProductExample);
        return brandMapper.updateByPrimaryKeySelective(pmsBrand);
    }

    @Override
    public int deleteBrand(Long id) {
        return brandMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<PmsBrand> listBrand(String keyword, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        PmsBrandExample pmsBrandExample = new PmsBrandExample();
        pmsBrandExample.setOrderByClause("sort desc");
        PmsBrandExample.Criteria criteria = pmsBrandExample.createCriteria();
        if (!StringUtils.isEmpty(keyword)){
            criteria.andNameLike("%"+keyword+"%");
        }
        return brandMapper.selectByExample(pmsBrandExample);
    }

    @Override
    public PmsBrand getBrand(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    @Override
    public int deleteBrand(List<Long> ids) {
        PmsBrandExample pmsBrandExample = new PmsBrandExample();
        PmsBrandExample.Criteria criteria = pmsBrandExample.createCriteria();
        criteria.andIdIn(ids);
        return brandMapper.deleteByExample(pmsBrandExample);
    }

    @Override
    public int updateShowStatus(List<Long> ids, Integer showStatus) {
        PmsBrand pmsBrand = new PmsBrand();
        pmsBrand.setShowStatus(showStatus);
        PmsBrandExample pmsBrandExample = new PmsBrandExample();
        pmsBrandExample.createCriteria().andIdIn(ids);
        return brandMapper.updateByExampleSelective(pmsBrand,pmsBrandExample);
    }

    @Override
    public int updateFactoryStatus(List<Long> ids, Integer factoryStatus) {
        PmsBrand pmsBrand = new PmsBrand();
        pmsBrand.setFactoryStatus(factoryStatus);
        PmsBrandExample pmsBrandExample = new PmsBrandExample();
        pmsBrandExample.createCriteria().andIdIn(ids);
        return brandMapper.updateByExampleSelective(pmsBrand,pmsBrandExample);
    }
}
