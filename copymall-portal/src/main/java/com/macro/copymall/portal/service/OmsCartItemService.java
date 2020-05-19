package com.macro.copymall.portal.service;

import com.macro.copymall.mbg.model.OmsCartItem;
import org.springframework.transaction.annotation.Transactional;

/**
 * 购物车管理Service
 */
public interface OmsCartItemService {
    /**
     * 查询购物车中是否包含该商品，有增加数量，无添加到购物车
     * @param cartItem
     * @return
     */
    @Transactional
    int add(OmsCartItem cartItem);
}
