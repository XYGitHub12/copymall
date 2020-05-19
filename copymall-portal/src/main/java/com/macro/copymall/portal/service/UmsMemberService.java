package com.macro.copymall.portal.service;

import com.macro.copymall.mbg.model.UmsMember;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

/**
 * 会员管理Service
 */
public interface UmsMemberService {
    /**
     * 获取验证码
     * @param telephone
     * @return
     */
    String getAuthCode(String telephone);

    /**
     * 会员注册
     * @param username
     * @param password
     * @param telephone
     * @param authCode
     */
    @Transactional
    void register(String username, String password, String telephone, String authCode);

    /**
     * 会员登录
     * @param username
     * @param password
     * @return
     */
    String login(String username, String password);

    /**
     * 获取当前登录的会员
     * @return
     */
    UmsMember getCurrentMember();

    /**
     * 根据用户名获取会员
     */
    UmsMember getByUsername(String username);

    /**
     * 根据会员id获取会员
     */
    UmsMember getById(Long id);

    /**
     * 获取用户信息
     */
    UserDetails loadUserByUsername(String username);

    /**
     * 修改密码
     * @param telephone
     * @param password
     * @param authCode
     */
    @Transactional
    void updatePassword(String telephone, String password, String authCode);

    /**
     * 刷新token
     * @param token
     * @return
     */
    String refreshToken(String token);

    /**
     * 根据会员id修改会员积分
     */
    void updateIntegration(Long id,Integer integration);
}
