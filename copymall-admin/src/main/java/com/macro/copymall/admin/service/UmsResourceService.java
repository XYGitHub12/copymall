package com.macro.copymall.admin.service;

import com.macro.copymall.mbg.model.UmsResource;

import java.util.List;

/**
 * 后台资源管理Service
 */
public interface UmsResourceService {
    /**
     * 查询全部资源
     * @return
     */
    List<UmsResource> listAll();
}
