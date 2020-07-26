package com.macro.copymall.admin.model;

import com.macro.copymall.mbg.model.PmsProductAttributeCategory;

import java.util.List;

/**
 * 包含有分类下属性的dto
 */
public class PmsProductAttributeCategoryItem extends PmsProductAttributeCategory{

    private List<PmsProductAttributeCategory> productAttributeList;

    public List<PmsProductAttributeCategory> getProductAttributeList() {
        return productAttributeList;
    }

    public void setProductAttributeList(List<PmsProductAttributeCategory> productAttributeList) {
        this.productAttributeList = productAttributeList;
    }
}
