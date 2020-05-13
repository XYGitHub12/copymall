package com.macro.copymall.portal.service;

/**
 * 会员信息缓存业务类
 * Created by macro on 2020/3/14.
 */
public interface UmsMemberCacheService {

    /**
     * 设置验证码
     */
    void setAuthCode(String telephone, String authCode);

}
