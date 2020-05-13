package com.macro.copymall.portal.service;

/**
 * 会员管理Service
 * Created by macro on 2018/8/3.
 */
public interface UmsMemberService {
    /**
     * 生成验证码
     */
    String generateAuthCode(String telephone);
}
