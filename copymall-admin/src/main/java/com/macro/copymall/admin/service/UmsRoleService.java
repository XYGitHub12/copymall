package com.macro.copymall.admin.service;

/**
 * 后台角色管理Service
 */
public interface UmsRoleService {
    /**
     * 根据管理员ID获取对应菜单
     * @param id
     * @return
     */
    Object getMenuList(Long id);
}
