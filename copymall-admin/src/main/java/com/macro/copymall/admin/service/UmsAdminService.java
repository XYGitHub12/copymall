package com.macro.copymall.admin.service;

import com.macro.copymall.admin.dto.UmsAdminParam;
import com.macro.copymall.admin.dto.UmsUpdatePasswordParam;
import com.macro.copymall.mbg.model.UmsAdmin;
import com.macro.copymall.mbg.model.UmsPermission;
import com.macro.copymall.mbg.model.UmsRole;

import java.util.List;

/**
 * 后台用户管理Service
 */
public interface UmsAdminService {
    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    String login(String username, String password);

    /**
     * 用户注册
     * @param umsAdminParam
     * @return
     */
    UmsAdmin register(UmsAdminParam umsAdminParam);

    /**
     * 查询指定用户信息
     * @param id
     * @return
     */
    UmsAdmin getItemById(Long id);

    /**
     * 修改指定用户信息
     * @param id
     * @param umsAdmin
     * @return
     */
    int updateById(Long id, UmsAdmin umsAdmin);

    /**
     * 修改指定用户密码
     * @param updatePasswordParam
     * @return
     */
    int updatePassword(UmsUpdatePasswordParam updatePasswordParam);

    /**
     * 删除指定用户信息
     * @param id
     * @return
     */
    int deleteById(Long id);

    /**
     * 为用户分配角色
     * @param adminId
     * @param roleIds
     * @return
     */
    int updateRole(Long adminId, List<Long> roleIds);

    /**
     * 获取指定用户的角色
     * @param adminId
     * @return
     */
    List<UmsRole> getRoleList(Long adminId);

    /**
     * 分页获取用户列表
     * @param keyword
     * @param pageSize
     * @param pageNum
     * @return
     */
    List<UmsAdmin> list(String keyword, Integer pageSize, Integer pageNum);

    /**
     * 给用户分配+-权限
     * @param adminId
     * @param permissionIds
     * @return
     */
    int updatePermission(Long adminId, List<Long> permissionIds);

    /**
     * 获取用户所有权限
     * @param adminId
     * @return
     */
    List<UmsPermission> getPermissionList(Long adminId);

    /**
     * 刷新token
     * @param token
     * @return
     */
    String refreshToken(String token);

    /**
     * 根据用户名获取后台管理员
     * @param username
     * @return
     */
    UmsAdmin getAdminByUsername(String username);
}
