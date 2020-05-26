package com.macro.copymall.portal.service.impl;

import com.macro.copymall.mbg.model.OmsCartItem;
import com.macro.copymall.mbg.model.PmsProductFullReduction;
import com.macro.copymall.mbg.model.PmsProductLadder;
import com.macro.copymall.mbg.model.PmsSkuStock;
import com.macro.copymall.portal.dao.PortalProductDao;
import com.macro.copymall.portal.domain.CartPromotionItem;
import com.macro.copymall.portal.domain.PromotionProduct;
import com.macro.copymall.portal.service.OmsPromotionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 促销信息管理Service实现类
 */
@Service
public class OmsPromotionServiceImpl implements OmsPromotionService {

    @Autowired
    private PortalProductDao portalProductDao;

    /**
     * 计算购物车中的促销活动信息
     * @param cartItems 购物车
     * @return
     */
    @Override
    public List<CartPromotionItem> calcCartPromotion(List<OmsCartItem> cartItems) {
        //1.获取分组结果，以SPU为单位计算优惠
        Map<Long,List<OmsCartItem>> productCartMap = groupCartItemBySpu(cartItems);
        //2.获取所有商品的促销信息
        List<PromotionProduct> promotionProductList = getPromotionProductList(cartItems);
        //3.根据商品促销类型计算商品促销优惠价格
        List<CartPromotionItem> cartPromotionItemList = new ArrayList<>();
        for (Map.Entry<Long,List<OmsCartItem>> entry : productCartMap.entrySet()){
            Long productId = entry.getKey();
            //根据商品id获取商品的促销信息
            PromotionProduct promotionProduct = getPromotionProductById(productId, promotionProductList);
            List<OmsCartItem> cartItemList = entry.getValue();
            //促销类型：0->没有促销使用原价;1->使用促销价；2->使用会员价；3->使用阶梯价格；4->使用满减价格；5->限时购
            Integer promotionType = promotionProduct.getPromotionType();
            if (promotionType == 1){
                //单品促销
                for (OmsCartItem cartItem : cartItemList){
                    CartPromotionItem cartPromotionItem = new CartPromotionItem();
                    //将cartItem赋值给cartPromotionItem
                    BeanUtils.copyProperties(cartItem,cartPromotionItem);
                    //1.封装促销种类名称
                    cartPromotionItem.setPromotionMessage("单品促销");
                    //获取库存
                    PmsSkuStock skuStock = getSku(promotionProduct, cartItem.getProductSkuId());
                    //获取商品原价
                    BigDecimal originalPrice = skuStock.getPrice();
                    //2.商品的真实价格=原价-促销价
                    cartPromotionItem.setReduceAmount(originalPrice.subtract(skuStock.getPromotionPrice()));
                    //3.商品的真实库存=剩余库存-锁定库存
                    cartPromotionItem.setRealStock(skuStock.getStock()-skuStock.getLockStock());
                    //4.购买商品赠送积分
                    cartPromotionItem.setIntegration(promotionProduct.getGiftPoint());
                    //5.购买商品赠送的成长值
                    cartPromotionItem.setGrowth(promotionProduct.getGiftGrowth());
                    cartPromotionItemList.add(cartPromotionItem);
                }
            }else if (promotionType == 3){
                //打折优惠
                //获取购物车指定商品的数量
                int count = getCartItemCount(cartItemList);
                //根据购买的商品数量获取满足条件的打折优惠策略
                PmsProductLadder ladder = getProductLadder(count, promotionProduct.getProductLadderList());
                if (ladder != null){
                    for (OmsCartItem cartItem : cartItemList){
                        CartPromotionItem cartPromotionItem = new CartPromotionItem();
                        BeanUtils.copyProperties(cartItem,cartPromotionItem);
                        //获取打折优惠的促销信息
                        String message = getLadderPromotionMessage(ladder);
                        //1.封装打折信息
                        cartPromotionItem.setPromotionMessage(message);
                        //获取库存
                        PmsSkuStock sku = getSku(promotionProduct, cartItem.getProductSkuId());
                        //获取商品原价
                        BigDecimal originalPrice = sku.getPrice();
                        BigDecimal reduceAmount = originalPrice.subtract(ladder.getDiscount().multiply(originalPrice));
                        //2.商品的折扣价
                        cartPromotionItem.setReduceAmount(reduceAmount);
                        //3.商品的真实库存=剩余库存-锁定库存
                        cartPromotionItem.setRealStock(sku.getStock()-sku.getLockStock());
                        //4.购买商品赠送积分
                        cartPromotionItem.setIntegration(promotionProduct.getGiftPoint());
                        //5.购买商品赠送的成长值
                        cartPromotionItem.setGrowth(promotionProduct.getGiftGrowth());
                        cartPromotionItemList.add(cartPromotionItem);
                    }
                }else {
                    handleNoReduce(cartPromotionItemList,cartItemList,promotionProduct);
                }
            }else if (promotionType == 4){
                //满减
                //获取购物车中指定商品的总价
                BigDecimal totalAmount= getCartItemAmount(cartItemList,promotionProductList);
                PmsProductFullReduction fullReduction = getProductFullReduction(totalAmount,promotionProduct.getProductFullReductionList());
                if(fullReduction != null){
                    for (OmsCartItem item : cartItemList) {
                        CartPromotionItem cartPromotionItem = new CartPromotionItem();
                        BeanUtils.copyProperties(item,cartPromotionItem);
                        String message = getFullReductionPromotionMessage(fullReduction);
                        cartPromotionItem.setPromotionMessage(message);
                        //(商品原价/总价)*满减金额
                        PmsSkuStock sku= getSku(promotionProduct, item.getProductSkuId());
                        BigDecimal originalPrice = sku.getPrice();
                        BigDecimal reduceAmount = originalPrice.divide(totalAmount, RoundingMode.HALF_EVEN).multiply(fullReduction.getReducePrice());
                        cartPromotionItem.setReduceAmount(reduceAmount);
                        cartPromotionItem.setRealStock(sku.getStock()-sku.getLockStock());
                        cartPromotionItem.setIntegration(promotionProduct.getGiftPoint());
                        cartPromotionItem.setGrowth(promotionProduct.getGiftGrowth());
                        cartPromotionItemList.add(cartPromotionItem);
                    }
                }else {
                    handleNoReduce(cartPromotionItemList,cartItemList,promotionProduct);
                }
            }else {
                //无优惠
                handleNoReduce(cartPromotionItemList,cartItemList,promotionProduct);
            }
        }
        return cartPromotionItemList;
    }

    /**
     * 获取满减促销消息
     */
    private String getFullReductionPromotionMessage(PmsProductFullReduction fullReduction) {
        StringBuilder builder = new StringBuilder();
        builder.append("满减优惠：");
        builder.append("满");
        builder.append(fullReduction.getFullPrice());
        builder.append("元，");
        builder.append("减");
        builder.append(fullReduction.getReducePrice());
        builder.append("元");
        return builder.toString();
    }

    /**
     * 根据购买的商品总价获取满足条件的满减优惠
     */
    private PmsProductFullReduction getProductFullReduction(BigDecimal totalAmount, List<PmsProductFullReduction> productFullReductionList) {
        //按条件从高到低排序
        productFullReductionList.sort(new Comparator<PmsProductFullReduction>() {
            @Override
            public int compare(PmsProductFullReduction o1, PmsProductFullReduction o2) {
                return o2.getFullPrice().subtract(o1.getFullPrice()).intValue();
            }
        });
        for(PmsProductFullReduction fullReduction : productFullReductionList){
            if(totalAmount.subtract(fullReduction.getFullPrice()).intValue() >= 0){
                return fullReduction;
            }
        }
        return null;
    }

    /**
     * 获取购物车中指定商品的总价
     */
    private BigDecimal getCartItemAmount(List<OmsCartItem> cartItemList, List<PromotionProduct> promotionProductList) {
        BigDecimal amount = new BigDecimal(0);
        for (OmsCartItem item : cartItemList) {
            //计算出商品原价
            PromotionProduct promotionProduct = getPromotionProductById(item.getProductId(), promotionProductList);
            PmsSkuStock sku = getSku(promotionProduct,item.getProductSkuId());
            amount = amount.add(sku.getPrice().multiply(new BigDecimal(item.getQuantity())));
        }
        return amount;
    }

    /**
     * 对未满足优惠条件的商品进行处理
     */
    private void handleNoReduce(List<CartPromotionItem> cartPromotionItemList, List<OmsCartItem> cartItemList, PromotionProduct promotionProduct) {
        for (OmsCartItem cartItem : cartItemList) {
            CartPromotionItem cartPromotionItem = new CartPromotionItem();
            BeanUtils.copyProperties(cartItem,cartPromotionItem);
            cartPromotionItem.setPromotionMessage("无优惠");
            cartPromotionItem.setReduceAmount(new BigDecimal(0));
            PmsSkuStock skuStock = getSku(promotionProduct,cartItem.getProductSkuId());
            if(skuStock!=null){
                cartPromotionItem.setRealStock(skuStock.getStock()-skuStock.getLockStock());
            }
            cartPromotionItem.setIntegration(promotionProduct.getGiftPoint());
            cartPromotionItem.setGrowth(promotionProduct.getGiftGrowth());
            cartPromotionItemList.add(cartPromotionItem);
        }
    }

    /**
     * 获取打折优惠的促销信息
     */
    private String getLadderPromotionMessage(PmsProductLadder ladder) {
        StringBuilder builder = new StringBuilder();
        builder.append("打折优惠：");
        builder.append("满");
        builder.append(ladder.getCount());
        builder.append("件，");
        builder.append("打");
        builder.append(ladder.getDiscount().multiply(new BigDecimal(10)));
        builder.append("折");
        return builder.toString();
    }

    /**
     * 根据购买的商品数量获取满足条件的打折优惠策略
     */
    private PmsProductLadder getProductLadder(int count, List<PmsProductLadder> productLadderList) {
        //按数量从大到小排序
        productLadderList.sort(new Comparator<PmsProductLadder>() {
            @Override
            public int compare(PmsProductLadder o1, PmsProductLadder o2) {
                return o2.getCount() - o1.getCount();
            }
        });
        for (PmsProductLadder productLadder : productLadderList){
            if (count >= productLadder.getCount()){
                return productLadder;
            }
        }
        return null;
    }

    /**
     * 获取购物车中指定商品的数量
     */
    private int getCartItemCount(List<OmsCartItem> cartItemList) {
        int count = 0;
        for (OmsCartItem cartItem : cartItemList){
            count += cartItem.getQuantity();
        }
        return count;
    }

    /**
     * 获取商品库存
     */
    private PmsSkuStock getSku(PromotionProduct promotionProduct, Long productSkuId) {
        for (PmsSkuStock skuStock : promotionProduct.getSkuStockList()){
            if (productSkuId.equals(skuStock.getId())){
                return skuStock;
            }
        }
        return null;
    }

    /**
     * 根据商品id获取商品的促销信息
     */
    private PromotionProduct getPromotionProductById(Long productId, List<PromotionProduct> promotionProductList) {
        for (PromotionProduct promotionProduct : promotionProductList){
            if (productId.equals(promotionProduct.getId())){
                return promotionProduct;
            }
        }
        return null;
    }

    /**
     * 获取所有商品的优惠信息
     */
    private List<PromotionProduct> getPromotionProductList(List<OmsCartItem> cartItems) {
        //创建一个商品ID的集合
        List<Long> productIDList = new ArrayList<>();
        //遍历购物车
        for (OmsCartItem cartItem : cartItems){
            //将商品ID添加到集合
            productIDList.add(cartItem.getProductId());
        }
        //根据商品ID查询所有优惠信息
        return portalProductDao.getPromotionProductList(productIDList);
    }

    /**
     *根据商品ID，以SPU为单位，对购物车进行分组
     * SPU：一个商品的多个款式的集合（比如iPhone6就是一个SPU，它包含了iPhone6的多种款式）
     * SKU：一件商品（比如ihone6银色16G就是一个SKU，是不可分割的最小存货单元）
     */
    private Map<Long,List<OmsCartItem>> groupCartItemBySpu(List<OmsCartItem> cartItems) {
        Map<Long,List<OmsCartItem>> productCartMap = new TreeMap<>();
        //遍历cartItems
        for (OmsCartItem cartItem : cartItems){
            //通过商品ID为key来获取购物车对应商品的列表
            List<OmsCartItem> productCartItemList = productCartMap.get(cartItem.getProductId());
            if (productCartItemList == null){
                //如果是null将购物车商品添加到List
                productCartItemList = new ArrayList<>();
                productCartItemList.add(cartItem);
                //再添加到map中，这样就对购物车中的商品进行了分组（以商品ID为key，可以定位到对应的一个或多个list）
                productCartMap.put(cartItem.getProductId(),productCartItemList);
            }else {
                productCartItemList.add(cartItem);
            }
        }
        return productCartMap;
    }

}
