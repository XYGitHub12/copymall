package com.macro.copymall.admin.service;

import com.macro.copymall.mbg.model.UmsAdmin;
import com.macro.copymall.mbg.model.UmsResource;

import java.util.List;

/**
 * 后台用户缓存操作Service
 */

public interface UmsAdminCacheService {

    /**
     * 获取缓存中的后台用户信息
     * @param username
     * @return
     */
    UmsAdmin getAdmin(String username);

    /**
     * 将用户信息存入缓存
     * @param admin
     */
    void setAdmin(UmsAdmin admin);

    void delAdmin(Long id);

    void delResourceList(Long id);

    List<UmsResource> getResourceList(Long adminId);

    void setResourceList(Long adminId, List<UmsResource> resourceList);

}
