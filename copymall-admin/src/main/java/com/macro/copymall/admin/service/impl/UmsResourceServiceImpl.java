package com.macro.copymall.admin.service.impl;

import com.macro.copymall.admin.service.UmsResourceService;
import com.macro.copymall.mbg.mapper.UmsResourceMapper;
import com.macro.copymall.mbg.model.UmsResource;
import com.macro.copymall.mbg.model.UmsResourceExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 后台资源管理Service实现类
 */
@Service
public class UmsResourceServiceImpl implements UmsResourceService {

    @Autowired
    private UmsResourceMapper umsResourceMapper;

    /**
     * 查询全部资源
     * @return
     */
    @Override
    public List<UmsResource> listAll() {
        return umsResourceMapper.selectByExample(new UmsResourceExample());
    }
}
