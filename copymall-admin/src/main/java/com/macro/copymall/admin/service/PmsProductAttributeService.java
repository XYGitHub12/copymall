package com.macro.copymall.admin.service;

import com.macro.copymall.admin.model.PmsProductAttributeParam;
import com.macro.copymall.admin.model.ProductAttrInfo;
import com.macro.copymall.mbg.model.PmsProductAttribute;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 商品属性Service
 */
public interface PmsProductAttributeService {
    /**
     * 根据分类分页获取商品属性
     * @param cid
     * @param type
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<PmsProductAttribute> getList(Long cid, Integer type, Integer pageNum, Integer pageSize);

    /**
     * 添加商品属性
     * @param pmsProductAttributeParam
     * @return
     */
    @Transactional
    int create(PmsProductAttributeParam pmsProductAttributeParam);

    /**
     * 修改商品属性
     * @param id
     * @param pmsProductAttributeParam
     * @return
     */
    int update(Long id, PmsProductAttributeParam pmsProductAttributeParam);

    /**
     * 获取单个商品属性
     * @param id
     * @return
     */
    PmsProductAttribute getItem(Long id);

    /**
     * 批量删除商品属性
     * @param ids
     * @return
     */
    int delete(List<Long> ids);

    /**
     * 根据商品分类的id获取商品属性及属性分类
     * @param productCategoryId
     * @return
     */
    List<ProductAttrInfo> getProductAttrInfo(Long productCategoryId);
}
