package com.macro.copymall.admin.service;


import com.macro.copymall.admin.dto.PmsBrandParam;
import com.macro.copymall.mbg.model.PmsBrand;

import java.util.List;

/**
 * 商品品牌Service
 */
public interface PmsBrandService {

    /**
     * 获取所有品牌
     * @return
     */
    List<PmsBrand> listAllBrand();

    /**
     * 添加品牌
     * @param pmsBrandParam
     * @return
     */
    int createBrand(PmsBrandParam pmsBrandParam);

    /**
     * 更新品牌
     * @param id
     * @param pmsBrandParam
     * @return
     */
    int updateBrand(Long id, PmsBrandParam pmsBrandParam);

    /**
     * 删除品牌
     * @param id
     * @return
     */
    int deleteBrand(Long id);

    /**
     *分页查询品牌
     * @param keyword
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<PmsBrand> listBrand(String keyword, Integer pageNum, Integer pageSize);

    /**
     * 获取品牌信息
     * @param id
     * @return
     */
    PmsBrand getBrand(Long id);

    /**
     * 批量删除品牌
     * @param ids
     * @return
     */
    int deleteBrand(List<Long> ids);

    /**
     * 批量更新品牌状态
     * @param ids
     * @param showStatus
     * @return
     */
    int updateShowStatus(List<Long> ids, Integer showStatus);

    /**
     * 批量更新厂家状态
     * @param ids
     * @param factoryStatus
     * @return
     */
    int updateFactoryStatus(List<Long> ids, Integer factoryStatus);
}
