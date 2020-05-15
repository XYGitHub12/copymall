package com.macro.copymall.admin.service.impl;

import com.macro.copymall.admin.service.UmsAdminCacheService;
import com.macro.copymall.mbg.model.UmsAdmin;
import com.macro.copymall.security.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 后台用户缓存操作Service实现类
 */
@Service
public class UmsAdminCacheServiceImpl implements UmsAdminCacheService{

    @Autowired
    private RedisService redisService;

    @Value("${redis.database}")
    private String REDIS_DATABASE;
    @Value("${redis.expire.common}")
    private Long REDIS_EXPIRE;
    @Value("${redis.key.admin}")
    private String REDIS_KEY_ADMIN;
    @Value("${redis.key.resourceList}")
    private String REDIS_KEY_RESOURCE_LIST;

    /**
     * 获取缓存中的用户信息
     * @param username
     * @return
     */
    @Override
    public UmsAdmin getAdmin(String username) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_ADMIN + ":" + username;
        return (UmsAdmin) redisService.get(key);
    }

    /**
     * 将用户信息存入缓存
     * @param admin
     */
    @Override
    public void setAdmin(UmsAdmin admin) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_ADMIN + ":" + admin.getUsername();
        redisService.set(key,admin,REDIS_EXPIRE);
    }
}
