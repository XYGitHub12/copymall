package com.macro.copymall.search.dao;

import com.macro.copymall.search.domain.EsProduct;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 搜索系统中的商品管理自定义Dao
 */
public interface EsProductDao {

    //获取数据库中的所有商品
    List<EsProduct> getAllEsProductList(@Param("id") Long id);
}
