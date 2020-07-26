package com.macro.copymall.admin.service;

import com.macro.copymall.admin.model.PmsProductAttributeCategoryItem;
import com.macro.copymall.mbg.model.PmsProductAttributeCategory;

import java.util.List;

/**
 * 商品属性分类Service
 */
public interface PmsProductAttributeCategoryService {
    /**
     * 添加商品属性分类
     * @param name
     * @return
     */
    int create(String name);

    /**
     * 商品属性分类修改
     * @param id
     * @param name
     * @return
     */
    int update(Long id, String name);

    /**
     * 删除单个属性分类
     * @param id
     * @return
     */
    int delete(Long id);

    /**
     * 获取单个商品属性分类列表
     * @param id
     * @return
     */
    PmsProductAttributeCategory getItem(Long id);

    /**
     * 分页获取所有商品分类
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<PmsProductAttributeCategory> getList(Integer pageNum, Integer pageSize);

    /**
     * 获取所有商品属性分类及其下属性
     * @return
     */
    List<PmsProductAttributeCategoryItem> getListWithAttr();
}
