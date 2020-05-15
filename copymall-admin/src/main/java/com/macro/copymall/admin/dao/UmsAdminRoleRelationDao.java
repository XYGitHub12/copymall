package com.macro.copymall.admin.dao;

import com.macro.copymall.mbg.model.UmsResource;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 自定义的用户角色Dao
 */
public interface UmsAdminRoleRelationDao {

    /**
     * 获取用户的所有角色
     * @param adminId
     * @return
     */
    List<UmsResource> getRoleList(@Param("adminId") Long adminId);
}
