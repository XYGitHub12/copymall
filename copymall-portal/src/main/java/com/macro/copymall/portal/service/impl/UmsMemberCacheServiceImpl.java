package com.macro.copymall.portal.service.impl;

import com.macro.copymall.portal.service.UmsMemberCacheService;
import com.macro.copymall.security.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * UmsMemberCacheService实现类
 * Created by macro on 2020/3/14.
 */
@Service
public class UmsMemberCacheServiceImpl implements UmsMemberCacheService {

    @Autowired
    private RedisService redisService;

    @Value("${redis.database}")
    private String REDIS_DATABASE;
    @Value("${redis.expire.common}")
    private Long REDIS_EXPIRE;
    @Value("${redis.expire.authCode}")
    private Long REDIS_EXPIRE_AUTH_CODE;
    @Value("${redis.key.member}")
    private String REDIS_KEY_MEMBER;
    @Value("${redis.key.authCode}")
    private String REDIS_KEY_AUTH_CODE;

    @Override
    public void setAuthCode(String telephone, String authCode) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_AUTH_CODE + ":" + telephone;
        System.out.println(key);
        redisService.set(key,authCode,REDIS_EXPIRE_AUTH_CODE);
    }

}
