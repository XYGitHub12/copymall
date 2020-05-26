package com.macro.copymall.portal.service;

import com.macro.copymall.mbg.model.OmsCartItem;
import com.macro.copymall.portal.domain.CartProduct;
import com.macro.copymall.portal.domain.CartPromotionItem;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 购物车管理Service
 */
public interface OmsCartItemService {
    /**
     *添加商品到购物车
     */
    @Transactional
    int add(OmsCartItem omsCartItem);

    /**
     * 根据会员id获取购物车列表
     */
    List<OmsCartItem> list(Long memberId);

    /**
     * 根据会员Id获取包含促销活动信息的购物车列表
     */
    List<CartPromotionItem> listPromotion(Long memberId);

    /**
     * 修改购物车商品的数量
     */
    int updateQuantity(Long id,Long memberId,Integer quantity);

    /**
     * 批量删除购物车中的商品
     */
    int delete(Long memberId,List<Long> ids);

    /**
     *获取购物车中用于选择商品规格的商品信息
     */
    CartProduct getCartProduct(Long productId);

    /**
     * 修改购物车中商品的规格
     */
    @Transactional
    int updateAttr(OmsCartItem omsCartItem);

    /**
     * 清空购物车
     */
    int clear(Long memberId);

}
