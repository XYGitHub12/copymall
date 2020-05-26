package com.macro.copymall.portal.dao;

import com.macro.copymall.portal.domain.CartProduct;
import com.macro.copymall.portal.domain.PromotionProduct;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 前台系统自定义Dao
 */
public interface PortalProductDao {
    /**
     * 查询商品优惠信息
     */
    List<PromotionProduct> getPromotionProductList(@Param("ids") List<Long> ids);

    /**
     * 获取购物车中用于选择商品规格的商品信息
     */
    CartProduct getCartProduct(@Param("id") Long id);
}
