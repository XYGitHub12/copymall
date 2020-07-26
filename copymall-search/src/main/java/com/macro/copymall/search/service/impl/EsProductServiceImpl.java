package com.macro.copymall.search.service.impl;

import com.macro.copymall.search.dao.EsProductDao;
import com.macro.copymall.search.domain.EsProduct;
import com.macro.copymall.search.repository.EsProductRepository;
import com.macro.copymall.search.service.EsProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 商品搜索管理Service的实现类
 */
@Service
public class EsProductServiceImpl implements EsProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EsProductServiceImpl.class);

    @Autowired
    private EsProductDao productDao;
    @Autowired
    private EsProductRepository productRepository;

    /**
     * 从数据库中导入所有商品到ES
     */
    @Override
    public int importAll() {
        //返回数据库的所有商品
        List<EsProduct> allEsProductList = productDao.getAllEsProductList(null);
        //保存到ES
        Iterable<EsProduct> products = productRepository.saveAll(allEsProductList);
        //遍历获取插入条数
        Iterator<EsProduct> iterator = products.iterator();
        int result = 0;
        if (iterator.hasNext()){
            result++;
            iterator.next();
        }
        return result;
    }

    /**
     * 根据id删除商品
     */
    @Override
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    /**
     * 根据id批量删除商品
     */
    @Override
    public void batch(List<Long> ids) {
        if (!CollectionUtils.isEmpty(ids)){
            ArrayList<EsProduct> list = new ArrayList<>();
            for(Long id : ids){
                EsProduct esProduct = new EsProduct();
                esProduct.setId(id);
                list.add(esProduct);
            }
            productRepository.deleteAll(list);
        }
    }

    /**
     * 根据id添加商品
     */
    @Override
    public EsProduct create(Long id) {
        EsProduct result = null;
        List<EsProduct> list = productDao.getAllEsProductList(id);
        if (list.size()>0){
            EsProduct product = list.get(0);
            result = productRepository.save(product);
        }
        return result;
    }

    /**
     * 简单搜索并分页
     * @param keyword
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public Page<EsProduct> search(String keyword, Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        return productRepository.findByNameOrSubTitleOrKeywords(keyword,keyword,keyword,pageable);
    }

    /**
     * 综合搜索
     * @param keyword
     * @param brandId
     * @param productCategoryId
     * @param pageNum
     * @param pageSize
     * @param sort
     * @return
     */
    @Override
    public Page<EsProduct> search(String keyword, Long brandId, Long productCategoryId, Integer pageNum, Integer pageSize, Integer sort) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        //分页
        nativeSearchQueryBuilder.withPageable(pageable);
        //过滤

        return null;
    }


}
