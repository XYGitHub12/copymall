package com.macro.copymall.portal.domain;

import com.macro.copymall.mbg.model.PmsProductAttribute;
import com.macro.copymall.mbg.model.PmsSkuStock;

import java.util.List;

/**
 * 封装购物车中选择规格的商品信息
 */
public class CartProduct {
    private List<PmsProductAttribute> productAttributeList;
    private List<PmsSkuStock> skuStockList;

    public List<PmsProductAttribute> getProductAttributeList() {
        return productAttributeList;
    }

    public void setProductAttributeList(List<PmsProductAttribute> productAttributeList) {
        this.productAttributeList = productAttributeList;
    }

    public List<PmsSkuStock> getSkuStockList() {
        return skuStockList;
    }

    public void setSkuStockList(List<PmsSkuStock> skuStockList) {
        this.skuStockList = skuStockList;
    }
}
