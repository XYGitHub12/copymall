package com.macro.copymall.portal.service.impl;

import com.macro.copymall.mbg.mapper.UmsMemberMapper;
import com.macro.copymall.mbg.model.UmsMember;
import com.macro.copymall.portal.service.UmsMemberCacheService;
import com.macro.copymall.security.annotation.CacheException;
import com.macro.copymall.security.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * UmsMemberCacheService实现类
 */
@Service
public class UmsMemberCacheServiceImpl implements UmsMemberCacheService {

    @Autowired
    private RedisService redisService;
    @Autowired
    private UmsMemberMapper umsMemberMapper;

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

    /**
     * 设置验证码
     * @param telephone
     * @param authCode
     */
    @CacheException
    @Override
    public void setAuthCode(String telephone, String authCode) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_AUTH_CODE + ":" + telephone;
        redisService.set(key,authCode,REDIS_EXPIRE_AUTH_CODE);
    }

    /**
     * 获取验证码
     * @param telephone
     * @return
     */
    @CacheException
    @Override
    public String getAuthCode(String telephone) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_AUTH_CODE + ":" + telephone;
        String authCode = (String) redisService.get(key);
        return authCode;
    }

    /**
     * 获取会员缓存
     * @param username
     * @return
     */
    @Override
    public UmsMember getMember(String username) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_MEMBER + ":" + username;
        return (UmsMember) redisService.get(key);
    }

    /**
     * 保存会员缓存
     * @param member
     */
    @Override
    public void setMember(UmsMember member) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_MEMBER + ":" + member.getUsername();
        redisService.set(key,member,REDIS_EXPIRE);
    }

    /**
     * 删除会员缓存
     * @param memberId
     */
    @Override
    public void delMember(Long memberId) {
        UmsMember umsMember = umsMemberMapper.selectByPrimaryKey(memberId);
        if (umsMember !=null){
            String key = REDIS_DATABASE + ":" + REDIS_KEY_MEMBER + ":" + umsMember.getUsername();
            redisService.del(key);
        }
    }

}
