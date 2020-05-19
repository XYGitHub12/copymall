package com.macro.copymall.portal.service;

import com.macro.copymall.mbg.model.UmsMember;

/**
 * 会员信息缓存业务类
 */
public interface UmsMemberCacheService {

    /**
     * 设置验证码
     */
    void setAuthCode(String telephone, String authCode);

    /**
     * 获取验证码
     * @param telephone
     * @return
     */
    String getAuthCode(String telephone);

    /**
     * 获取会员
     * @param username
     * @return
     */
    UmsMember getMember(String username);

    /**
     * 保存会员
     * @param member
     */
    void setMember(UmsMember member);

    /**
     * 删除会员用户缓存
     */
    void delMember(Long memberId);

}
