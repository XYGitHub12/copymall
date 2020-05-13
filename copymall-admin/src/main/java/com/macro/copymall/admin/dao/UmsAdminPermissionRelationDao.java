package com.macro.copymall.admin.dao;

import com.macro.copymall.mbg.model.UmsAdminPermissionRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List; /**
 * 用户权限自定义Dao
 */
public interface UmsAdminPermissionRelationDao {
    /**
     * 批量创建
     * @param list
     * @return
     */
    int insertList(@Param("list") List<UmsAdminPermissionRelation> list);
}
