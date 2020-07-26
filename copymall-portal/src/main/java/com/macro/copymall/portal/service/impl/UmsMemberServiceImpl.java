package com.macro.copymall.portal.service.impl;

import com.macro.copymall.common.exception.Asserts;
import com.macro.copymall.mbg.mapper.UmsMemberLevelMapper;
import com.macro.copymall.mbg.mapper.UmsMemberMapper;
import com.macro.copymall.mbg.model.UmsMember;
import com.macro.copymall.mbg.model.UmsMemberExample;
import com.macro.copymall.mbg.model.UmsMemberLevel;
import com.macro.copymall.mbg.model.UmsMemberLevelExample;
import com.macro.copymall.portal.domain.MemberDetails;
import com.macro.copymall.portal.service.UmsMemberCacheService;
import com.macro.copymall.portal.service.UmsMemberService;
import com.macro.copymall.security.util.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 会员管理Service实现类
 */
@Service
public class UmsMemberServiceImpl implements UmsMemberService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UmsMemberServiceImpl.class);

    @Autowired
    private UmsMemberCacheService memberCacheService;
    @Autowired
    private UmsMemberMapper memberMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UmsMemberLevelMapper memberLevelMapper;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    //获取验证码
    @Override
    public String getAuthCode(String telephone) {
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        for (int i=0;i<6;i++){
            builder.append(random.nextInt(10));
        }
        String authCode = builder.toString();
        //把验证码存入缓存
        memberCacheService.setAuthCode(telephone,authCode);
        return authCode;
    }

    //验证验证码
    private boolean verifyAuthCode(String authCode, String telephone) {
        if (StringUtils.isEmpty(authCode)){
            return false;
        }
        //去缓存中查
        String realAuthCode = memberCacheService.getAuthCode(telephone);
        return authCode.equals(realAuthCode);
    }

    //会员注册
    @Override
    public void register(String username, String password, String telephone, String authCode) {
        //判断验证码是否正确
        if (!verifyAuthCode(authCode,telephone)){
            Asserts.fail("验证码错误");
        }
        //验证用户名是否存在/验证手机号码是否已经被注册
        UmsMemberExample memberExample = new UmsMemberExample();
        memberExample.createCriteria().andUsernameEqualTo(username);
        memberExample.or(memberExample.createCriteria().andPhoneEqualTo(telephone));
        List<UmsMember> members = memberMapper.selectByExample(memberExample);
        if (CollectionUtils.isEmpty(members)){
            Asserts.fail("该用户已经存在");
        }
        //进行添加操作
        UmsMember member = new UmsMember();
        member.setUsername(username);
        member.setPassword(passwordEncoder.encode(password));
        member.setPhone(telephone);
        member.setStatus(1);
        member.setCreateTime(new Date());
        //获取默认的会员等级并添加
        UmsMemberLevelExample memberLevelExample = new UmsMemberLevelExample();
        memberLevelExample.createCriteria().andDefaultStatusEqualTo(1);
        List<UmsMemberLevel> memberLevels = memberLevelMapper.selectByExample(memberLevelExample);
        if (CollectionUtils.isEmpty(memberLevels)){
            member.setMemberLevelId(memberLevels.get(0).getId());
        }
        memberMapper.insert(member);
        //设置密码为null，去登录
        member.setPassword(null);
    }

    //会员登录
    @Override
    public String login(String username, String password) {
        String token = null;
        try {
            //获取会员详情（预置信息）
            UserDetails userDetails = loadUserByUsername(username);
            //判断密码是否正确
            if (!passwordEncoder.matches(password,userDetails.getPassword())){
                throw new BadCredentialsException("密码错误");
            }
            //认证
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            //用来存放authentication(认证成功后保存的访问者信息,保存到上下文)
            SecurityContextHolder.getContext().setAuthentication(authentication);
            //生成token
            token = jwtTokenUtil.generateToken(userDetails);
        }catch (AuthenticationException e){
            LOGGER.warn("登录异常：{}",e.getMessage());
        }
        return token;
    }

    //获取当前登录会员
    @Override
    public UmsMember getCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();
        return memberDetails.getUmsMember();
    }

    //获取会员详情
    @Override
    public UserDetails loadUserByUsername(String username) {
        //根据用户名获取会员
        UmsMember member = getByUsername(username);
        if (member != null){
            //生成会员详情（Securit需要的）
            return new MemberDetails(member);
        }
        throw new UsernameNotFoundException("用户名或密码错误");
    }

    //根据用户名获取会员
    @Override
    public UmsMember getByUsername(String username) {
        //查缓存
        UmsMember member = memberCacheService.getMember(username);
        if (member != null){
            return member;
        }
        //查数据库
        UmsMemberExample memberExample = new UmsMemberExample();
        memberExample.createCriteria().andUsernameEqualTo(username);
        List<UmsMember> members = memberMapper.selectByExample(memberExample);
        //更新到缓存
        if (!CollectionUtils.isEmpty(members)){
            member = members.get(0);
            memberCacheService.setMember(member);
            return member;
        }
        return null;
    }

    //根据会员ID获取会员
    @Override
    public UmsMember getById(Long id) {
        return memberMapper.selectByPrimaryKey(id);
    }

    //修改密码
    @Override
    public void updatePassword(String telephone, String password, String authCode) {
        //查找用户是否存在
        UmsMemberExample memberExample = new UmsMemberExample();
        memberExample.createCriteria().andPhoneEqualTo(telephone);
        List<UmsMember> members = memberMapper.selectByExample(memberExample);
        if (CollectionUtils.isEmpty(members)){
            Asserts.fail("该账号不存在");
        }
        //校验验证码
        if (!verifyAuthCode(authCode,telephone)){
            Asserts.fail("验证码错误");
        }
        //更新数据库
        UmsMember member = members.get(0);
        member.setPassword(passwordEncoder.encode(password));
        //updateByPrimaryKeySelective会对字段进行判断后更新，如果是null就不会覆盖
        //updateByPrimaryKey对全部字段更新，如果是null会覆盖
        memberMapper.updateByPrimaryKeySelective(member);
        //删除缓存
        memberCacheService.delMember(member.getId());
    }

    //刷新token
    @Override
    public String refreshToken(String token) {
        return jwtTokenUtil.refreshHeadToken(token);
    }

    //根据会员id修改积分
    @Override
    public void updateIntegration(Long id, Integer integration) {
        UmsMember member = new UmsMember();
        member.setId(id);
        member.setIntegration(integration);
        memberMapper.updateByPrimaryKeySelective(member);
        memberCacheService.delMember(id);
    }
}
