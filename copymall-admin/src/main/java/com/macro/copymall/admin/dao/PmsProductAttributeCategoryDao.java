package com.macro.copymall.admin.dao;

import com.macro.copymall.admin.dto.PmsProductAttributeCategoryItem;

import java.util.List;

/**
 * 自定义商品属性分类Dao
 */
public interface PmsProductAttributeCategoryDao {
    /**
     * 获取包含属性的商品属性分类
     * @return
     */
    List<PmsProductAttributeCategoryItem> getListWithAttr();
}
