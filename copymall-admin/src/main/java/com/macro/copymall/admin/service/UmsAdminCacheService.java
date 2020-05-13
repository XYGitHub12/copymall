package com.macro.copymall.admin.service;

import com.macro.copymall.mbg.model.UmsAdmin;
import com.macro.copymall.mbg.model.UmsResource;

import java.util.List;

/**
 * 后台用户缓存操作Service
 */
public interface UmsAdminCacheService {
    UmsAdmin getAdmin(String username);

    void setAdmin(UmsAdmin admin);

    void delAdmin(Long id);

    void delResourceList(Long id);

    List<UmsResource> getResourceList(Long adminId);

    void setResourceList(Long adminId, List<UmsResource> resourceList);
}
