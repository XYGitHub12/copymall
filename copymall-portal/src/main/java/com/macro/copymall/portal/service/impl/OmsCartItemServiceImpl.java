package com.macro.copymall.portal.service.impl;

import com.macro.copymall.mbg.mapper.OmsCartItemMapper;
import com.macro.copymall.mbg.model.OmsCartItem;
import com.macro.copymall.mbg.model.OmsCartItemExample;
import com.macro.copymall.mbg.model.UmsMember;
import com.macro.copymall.portal.dao.PortalProductDao;
import com.macro.copymall.portal.domain.CartProduct;
import com.macro.copymall.portal.domain.CartPromotionItem;
import com.macro.copymall.portal.service.OmsCartItemService;
import com.macro.copymall.portal.service.OmsPromotionService;
import com.macro.copymall.portal.service.UmsMemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 购物车管理Service实现类
 */
@Service
public class OmsCartItemServiceImpl implements OmsCartItemService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OmsCartItemServiceImpl.class);

    @Autowired
    private UmsMemberService memberService;
    @Autowired
    private OmsCartItemMapper cartItemMapper;
    @Autowired
    private OmsPromotionService promotionService;
    @Autowired
    private PortalProductDao portalProductDao;

    /**
     *  查询购物车中是否有该商品，有添加数量，没有就新增购物车
     */
    @Override
    public int add(OmsCartItem omsCartItem) {
        int count;
        //获取当前登录会员
        UmsMember currentMember = memberService.getCurrentMember();
        //将当前会员的信息封装到OmsCartItem
        omsCartItem.setMemberId(currentMember.getId());
        omsCartItem.setMemberNickname(currentMember.getNickname());
        omsCartItem.setDeleteStatus(0);
        //获取购物车
        OmsCartItem exitsCart = getCartItem(omsCartItem);
        if (exitsCart == null) {
            //添加商品到购物车
            omsCartItem.setCreateDate(new Date());
            count = cartItemMapper.insert(omsCartItem);
        } else {
            //添加商品数量
            omsCartItem.setQuantity(exitsCart.getQuantity() + omsCartItem.getQuantity());
            omsCartItem.setModifyDate(new Date());
            count = cartItemMapper.updateByPrimaryKey(omsCartItem);
        }
        return count;
    }

    /**
     * 根据会员ID，商品ID等参数获取购物车
     * @param omsCartItem
     * @return
     */
    private OmsCartItem getCartItem(OmsCartItem omsCartItem){
        //拼接查询
        OmsCartItemExample example = new OmsCartItemExample();
        OmsCartItemExample.Criteria criteria = example.createCriteria()
                .andMemberIdEqualTo(omsCartItem.getMemberId())
                .andProductIdEqualTo(omsCartItem.getProductId())
                .andDeleteStatusEqualTo(0);
        if (!StringUtils.isEmpty(omsCartItem.getProductSkuId())){
            criteria.andProductSkuIdEqualTo(omsCartItem.getProductSkuId());
        }
        List<OmsCartItem> exitsCart = cartItemMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(exitsCart)){
            return exitsCart.get(0);
        }
        return null;
    }

    /**
     * 根据会员id获取购物车列表
     * @param memberId
     * @return
     */
    @Override
    public List<OmsCartItem> list(Long memberId) {
        //获取购物车（需要获取未被删除的）
        OmsCartItemExample example = new OmsCartItemExample();
        example.createCriteria()
                .andMemberIdEqualTo(memberId)
                .andDeleteStatusEqualTo(0);
        return cartItemMapper.selectByExample(example);
    }

    /**
     * 根据会员Id获取包含促销活动信息的购物车列表
     * @param memberId
     * @return
     */
    @Override
    public List<CartPromotionItem> listPromotion(Long memberId) {
        //获取当前用户的购物车列表
        List<OmsCartItem> cartItems = list(memberId);
        //根据购物车列表获取促销信息放入集合中
        List<CartPromotionItem> cartPromotionItems = new ArrayList<>();
        if (!CollectionUtils.isEmpty(cartItems)){
            cartPromotionItems = promotionService.calcCartPromotion(cartItems);
        }
        return cartPromotionItems;
    }

    /**
     * 修改购物车中商品数量
     * @param id
     * @param memberId
     * @param quantity
     * @return
     */
    @Override
    public int updateQuantity(Long id, Long memberId, Integer quantity) {
        OmsCartItem cartItem = new OmsCartItem();
        cartItem.setQuantity(quantity);
        OmsCartItemExample example = new OmsCartItemExample();
        example.createCriteria()
                .andIdEqualTo(id)
                .andMemberIdEqualTo(memberId)
                .andDeleteStatusEqualTo(0);
        return cartItemMapper.updateByExampleSelective(cartItem,example);
    }

    /**
     * 批量删除
     * @param memberId
     * @param ids
     * @return
     */
    @Override
    public int delete(Long memberId, List<Long> ids) {
        OmsCartItem record = new OmsCartItem();
        record.setDeleteStatus(1);
        OmsCartItemExample example = new OmsCartItemExample();
        example.createCriteria()
                .andIdIn(ids)
                .andMemberIdEqualTo(memberId);
        return cartItemMapper.updateByExampleSelective(record,example);
    }

    /**
     * 获取购物车中用于选择商品规格的商品信息
     * @param productId
     * @return
     */
    @Override
    public CartProduct getCartProduct(Long productId) {
        return portalProductDao.getCartProduct(productId);
    }

    /**
     * 修改购物车中商品的规格
     * @param omsCartItem
     * @return
     */
    @Override
    public int updateAttr(OmsCartItem omsCartItem) {
        //删除原购物车信息
        OmsCartItem updateCart = new OmsCartItem();
        updateCart.setId(omsCartItem.getId());
        updateCart.setModifyDate(new Date());
        updateCart.setDeleteStatus(1);
        cartItemMapper.updateByPrimaryKeySelective(updateCart);
        omsCartItem.setId(null);
        add(omsCartItem);
        return 1;
    }

    /**
     * 清空购物车
     * @param memberId
     * @return
     */
    @Override
    public int clear(Long memberId) {
        OmsCartItem record = new OmsCartItem();
        record.setDeleteStatus(1);
        OmsCartItemExample example = new OmsCartItemExample();
        example.createCriteria()
                .andMemberIdEqualTo(memberId);
        return cartItemMapper.updateByExampleSelective(record,example);
    }

}
