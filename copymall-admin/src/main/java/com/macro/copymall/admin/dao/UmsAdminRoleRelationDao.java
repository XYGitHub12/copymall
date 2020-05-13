package com.macro.copymall.admin.dao;

import com.macro.copymall.mbg.model.UmsResource;
import com.macro.copymall.mbg.model.UmsAdminRoleRelation;
import com.macro.copymall.mbg.model.UmsPermission;
import com.macro.copymall.mbg.model.UmsRole;

import java.util.List; /**
 * 后台用户与角色管理自定义Dao
 */
public interface UmsAdminRoleRelationDao {
    /**
     * 批量插入用户角色关系
     * @param list
     */
    void insertList(List<UmsAdminRoleRelation> list);

    /**
     * 获取指定用户的角色
     * @param adminId
     * @return
     */
    List<UmsRole> getRoleList(Long adminId);

    /**
     * 获取用户所有角色权限
     * @param adminId
     * @return
     */
    List<UmsPermission> getRolePermissionList(Long adminId);

    /**
     * 获取用户所有权限(包括+-权限)
     * @param adminId
     * @return
     */
    List<UmsPermission> getPermissionList(Long adminId);

    /**
     * 获取用户所有可访问资源
     * @param adminId
     * @return
     */
    List<UmsResource> getResourceList(Long adminId);
}
