package com.macro.copymall.portal.service;

import com.macro.copymall.mbg.model.OmsCartItem;
import com.macro.copymall.portal.domain.CartPromotionItem;

import java.util.List;

/**
 * 促销信息管理Service
 */
public interface OmsPromotionService {
    /**
     * 计算购物车中的促销活动信息
     * @param cartItems 购物车
     */
    List<CartPromotionItem> calcCartPromotion(List<OmsCartItem> cartItems);
}
