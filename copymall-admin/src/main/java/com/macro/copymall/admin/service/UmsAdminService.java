package com.macro.copymall.admin.service;

import com.macro.copymall.admin.dto.UmsAdminParam;
import com.macro.copymall.mbg.model.UmsAdmin;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 会员后台管理Service
 */
public interface UmsAdminService {
    /**
     * 获取用户信息
     * @param username
     * @return
     */
    UserDetails loadUserByUsername(String username);

    /**
     * 用户注册
     * @param umsAdminParam
     * @return
     */
    UmsAdmin register(UmsAdminParam umsAdminParam);

    /**
     * 用户登录并返回token
     * @param username
     * @param password
     * @return
     */
    String login(String username, String password);
}
