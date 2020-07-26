package com.macro.copymall.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.copymall.admin.dao.PmsProductAttributeCategoryDao;
import com.macro.copymall.admin.model.PmsProductAttributeCategoryItem;
import com.macro.copymall.admin.service.PmsProductAttributeCategoryService;
import com.macro.copymall.mbg.mapper.PmsProductAttributeCategoryMapper;
import com.macro.copymall.mbg.model.PmsProductAttributeCategory;
import com.macro.copymall.mbg.model.PmsProductAttributeCategoryExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * PmsProductAttributeCategoryService实现类
 */
@Service
public class PmsProductAttributeCategoryServiceImpl implements PmsProductAttributeCategoryService {

    @Autowired
    private PmsProductAttributeCategoryMapper productAttributeCategoryMapper;

    @Autowired
    private PmsProductAttributeCategoryDao productAttributeCategoryDao;

    @Override
    public int create(String name) {
        PmsProductAttributeCategory productAttributeCategory = new PmsProductAttributeCategory();
        productAttributeCategory.setName(name);
        return productAttributeCategoryMapper.insertSelective(productAttributeCategory);
    }

    @Override
    public int update(Long id, String name) {
        PmsProductAttributeCategory productAttributeCategory = new PmsProductAttributeCategory();
        productAttributeCategory.setName(name);
        productAttributeCategory.setId(id);
        return productAttributeCategoryMapper.updateByPrimaryKeySelective(productAttributeCategory);
    }

    @Override
    public int delete(Long id) {
        return productAttributeCategoryMapper.deleteByPrimaryKey(id);
    }

    @Override
    public PmsProductAttributeCategory getItem(Long id) {
        return productAttributeCategoryMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<PmsProductAttributeCategory> getList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        PmsProductAttributeCategoryExample productAttributeCategoryExample = new PmsProductAttributeCategoryExample();
        List<PmsProductAttributeCategory> productAttributeCategoryList = productAttributeCategoryMapper.selectByExample(productAttributeCategoryExample);
        return productAttributeCategoryList;
    }

    @Override
    public List<PmsProductAttributeCategoryItem> getListWithAttr() {
        return productAttributeCategoryDao.getListWithAttr();
    }
}
