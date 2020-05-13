package com.macro.copymall.search.service;

import com.macro.copymall.search.domain.EsProduct;
import org.springframework.data.domain.Page;

import java.util.List; /**
 * 商品搜索管理Service
 * Created by macro on 2018/6/19.
 */
public interface EsProductService {
    /**
     * 从数据库中导入所有商品到ES
     */
    int importAll();

    /**
     * 根据id删除商品
     * @param id
     */
    void delete(Long id);

    /**
     * 根据ID批量删除商品
     * @param ids
     */
    void batch(List<Long> ids);

    /**
     * 根据id添加商品
     * @param id
     * @return
     */
    EsProduct create(Long id);

    /**
     * 简单搜索
     * @param keyword
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<EsProduct> search(String keyword, Integer pageNum, Integer pageSize);
}
