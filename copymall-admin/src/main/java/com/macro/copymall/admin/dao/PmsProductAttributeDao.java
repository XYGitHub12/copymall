package com.macro.copymall.admin.dao;

import com.macro.copymall.admin.dto.ProductAttrInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 自定义商品属性Dao
 */
public interface PmsProductAttributeDao {
    /**
     * 获取商品属性信息
     * @param productCategoryId
     * @return
     */
    List<ProductAttrInfo> getProductAttrInfo(@Param("id") Long productCategoryId);
}
