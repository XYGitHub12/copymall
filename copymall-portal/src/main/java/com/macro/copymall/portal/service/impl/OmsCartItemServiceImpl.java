package com.macro.copymall.portal.service.impl;

import com.macro.copymall.mbg.mapper.OmsCartItemMapper;
import com.macro.copymall.mbg.model.OmsCartItem;
import com.macro.copymall.mbg.model.OmsCartItemExample;
import com.macro.copymall.mbg.model.UmsMember;
import com.macro.copymall.portal.dao.PortalProductDao;
import com.macro.copymall.portal.model.CartProduct;
import com.macro.copymall.portal.model.CartPromotionItem;
import com.macro.copymall.portal.service.OmsCartItemService;
import com.macro.copymall.portal.service.OmsPromotionService;
import com.macro.copymall.portal.service.UmsMemberService;
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
public class OmsCartItemServiceImpl implements OmsCartItemService{

    @Autowired
    private OmsCartItemMapper cartItemMapper;
    @Autowired
    private UmsMemberService memberService;
    @Autowired
    private PortalProductDao portalProductDao;
    @Autowired
    private OmsPromotionService promotionService;

    //添加商品到购物车
    @Override
    public int add(OmsCartItem omsCartItem) {
        int count;
        UmsMember currentMember = memberService.getCurrentMember();
        //封装当前会员信息
        omsCartItem.setMemberId(currentMember.getId());
        omsCartItem.setMemberNickname(currentMember.getNickname());
        omsCartItem.setDeleteStatus(0);
        //查询购物车是否有该商品
        OmsCartItem exitsCart = getCartItem(omsCartItem);
        if (exitsCart == null){
            //添加商品
            omsCartItem.setCreateDate(new Date());
            count = cartItemMapper.insert(omsCartItem);
        }else {
            //修改商品数量
            omsCartItem.setQuantity(exitsCart.getQuantity()+omsCartItem.getQuantity());
            omsCartItem.setModifyDate(new Date());
            count = cartItemMapper.updateByPrimaryKey(omsCartItem);
        }
        return count;
    }

    //根据会员id获取购物车列表
    @Override
    public List<OmsCartItem> list(Long memberId) {
        OmsCartItemExample cartItemExample = new OmsCartItemExample();
        cartItemExample.createCriteria().andMemberIdEqualTo(memberId).andDeleteStatusEqualTo(0);
        return cartItemMapper.selectByExample(cartItemExample);
    }

    //根据会员Id获取包含促销活动信息的购物车列表
    @Override
    public List<CartPromotionItem> listPromotion(Long memberId) {
        List<OmsCartItem> cartItems = list(memberId);
        List<CartPromotionItem> cartPromotionItems = new ArrayList<>();
        if (!CollectionUtils.isEmpty(cartItems)){
            cartPromotionItems = promotionService.calcCartPromotion(cartItems);
        }
        return cartPromotionItems;
    }

    //根据会员ID，商品ID等参数获取购物车
    private OmsCartItem getCartItem(OmsCartItem omsCartItem) {
        OmsCartItemExample cartItemExample = new OmsCartItemExample();
        OmsCartItemExample.Criteria criteria = cartItemExample.createCriteria()
                .andMemberIdEqualTo(omsCartItem.getMemberId())
                .andProductIdEqualTo(omsCartItem.getProductId())
                .andDeleteStatusEqualTo(0);
        if (!StringUtils.isEmpty(omsCartItem.getProductSkuId())){
            criteria.andProductSkuIdEqualTo(omsCartItem.getProductSkuId());
        }
        List<OmsCartItem> cartItemList = cartItemMapper.selectByExample(cartItemExample);
        if (!CollectionUtils.isEmpty(cartItemList)){
            return cartItemList.get(0);
        }
        return null;
    }


    //修改商品数量
    @Override
    public int updateQuantity(Long id, Long memberId, Integer quantity) {
        OmsCartItem record = new OmsCartItem();
        record.setQuantity(quantity);
        OmsCartItemExample cartItemExample = new OmsCartItemExample();
        cartItemExample.createCriteria().andIdEqualTo(id).andMemberIdEqualTo(memberId).andDeleteStatusEqualTo(0);
        return cartItemMapper.updateByExampleSelective(record,cartItemExample);
    }


    //批量删除购物车中的商品
    @Override
    public int delete(Long memberId, List<Long> ids) {
        OmsCartItem record = new OmsCartItem();
        record.setDeleteStatus(1);
        OmsCartItemExample cartItemExample = new OmsCartItemExample();
        cartItemExample.createCriteria().andIdIn(ids).andMemberIdEqualTo(memberId);
        return cartItemMapper.updateByExampleSelective(record,cartItemExample);
    }

    //获取购物车中用于选择商品规格的商品信息
    @Override
    public CartProduct getCartProduct(Long productId) {
        return portalProductDao.getCartProduct(productId);
    }

    @Override
    public int updateAttr(OmsCartItem omsCartItem) {
        int count = cartItemMapper.updateByPrimaryKeySelective(omsCartItem);
        return count;
    }

    //清空购物车
    @Override
    public int clear(Long memberId) {
        OmsCartItem record = new OmsCartItem();
        record.setDeleteStatus(1);
        OmsCartItemExample cartItemExample = new OmsCartItemExample();
        cartItemExample.createCriteria().andMemberIdEqualTo(memberId);
        return cartItemMapper.updateByExampleSelective(record,cartItemExample);
    }
}
