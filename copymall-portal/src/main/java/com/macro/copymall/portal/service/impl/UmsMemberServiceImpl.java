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
    private UmsMemberCacheService umsMemberCacheService;
    @Autowired
    private UmsMemberMapper umsMemberMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UmsMemberLevelMapper umsMemberLevelMapper;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * 获取验证码
     * @param telephone
     * @return
     */
    @Override
    public String getAuthCode(String telephone) {
        StringBuilder builder = new StringBuilder();
        //Random获取10以内的6位数
        Random random = new Random();
        for (int i=0;i<6;i++){
            builder.append(random.nextInt(10));
        }
        String authCode = builder.toString();
        //把验证码存入缓存
        umsMemberCacheService.setAuthCode(telephone,authCode);
        return authCode;
    }

    /**
     * 会员注册
     * @param username
     * @param password
     * @param telephone
     * @param authCode
     */
    @Override
    public void register(String username, String password, String telephone, String authCode) {
        //判断验证码是否正确
        if (!verifyAuthCode(authCode,telephone)){
            Asserts.fail("验证码错误");
        }
        //判断用户名是否存在/手机号码是否已经被注册
        UmsMemberExample umsMemberExample = new UmsMemberExample();
        umsMemberExample.createCriteria().andUsernameEqualTo(username);
        umsMemberExample.or(umsMemberExample.createCriteria().andPhoneEqualTo(telephone));
        List<UmsMember> umsMembers = umsMemberMapper.selectByExample(umsMemberExample);
        if (!CollectionUtils.isEmpty(umsMembers)){
            Asserts.fail("该用户已经存在");
        }
        //进行添加操作
        UmsMember umsMember = new UmsMember();
        umsMember.setUsername(username);
        umsMember.setPassword(passwordEncoder.encode(password));
        umsMember.setPhone(telephone);
        umsMember.setCreateTime(new Date());
        umsMember.setStatus(1);
        //获取默认的会员等级
        UmsMemberLevelExample umsMemberLevelExample = new UmsMemberLevelExample();
        umsMemberLevelExample.createCriteria().andDefaultStatusEqualTo(1);
        List<UmsMemberLevel> umsMemberLevels = umsMemberLevelMapper.selectByExample(umsMemberLevelExample);
        if (!CollectionUtils.isEmpty(umsMemberLevels)){
            umsMember.setMemberLevelId(umsMemberLevels.get(0).getId());
        }
        umsMemberMapper.insert(umsMember);
        //设置密码为null，去登录
        umsMember.setPassword(null);
    }

    //对输入的验证码进行校验
    private boolean verifyAuthCode(String authCode, String telephone){
        if(StringUtils.isEmpty(authCode)){
            return false;
        }
        String realAuthCode = umsMemberCacheService.getAuthCode(telephone);
        return authCode.equals(realAuthCode);
    }

    /**
     * 会员登录
     * @param username
     * @param password
     * @return
     */
    @Override
    public String login(String username, String password) {
        String token = null;
        try {
            //获取会员详情
            UserDetails userDetails = loadUserByUsername(username);
            //判断密码是否正确
            if (!passwordEncoder.matches(password,userDetails.getPassword())){
                throw new BadCredentialsException("密码不正确");
            }
            //认证
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
            //用来存放authentication(认证成功后保存的访问者信息)
            SecurityContextHolder.getContext().setAuthentication(authentication);
            //生成token
            token = jwtTokenUtil.generateToken(userDetails);
        }catch (AuthenticationException e){
            LOGGER.warn("登录异常:{}",e.getMessage());
        }
        return token;
    }

    /**
     * 获取当前登录会员
     * @return
     */
    @Override
    public UmsMember getCurrentMember() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();
        return memberDetails.getUmsMember();
    }

    /**
     * 获取会员详情
     * @param username
     * @return
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        //根据用户名获取会员
        UmsMember member = getByUsername(username);
        if(member!=null){
            //生成会员详情（Securit需要的）
            return new MemberDetails(member);
        }
        throw new UsernameNotFoundException("用户名或密码错误");
    }

    /**
     * 修改密码
     * @param telephone
     * @param password
     * @param authCode
     */
    @Override
    public void updatePassword(String telephone, String password, String authCode) {
        //查找用户是否存在
        UmsMemberExample umsMemberExample = new UmsMemberExample();
        umsMemberExample.createCriteria().andPhoneEqualTo(telephone);
        List<UmsMember> umsMembers = umsMemberMapper.selectByExample(umsMemberExample);
        if (CollectionUtils.isEmpty(umsMembers)){
            Asserts.fail("该账号不存在");
        }
        //验证验证码
        if (!verifyAuthCode(authCode,telephone)){
            Asserts.fail("验证码错误");
        }
        UmsMember umsMember = umsMembers.get(0);
        umsMember.setPassword(passwordEncoder.encode(password));
        //更新数据库
        umsMemberMapper.updateByPrimaryKeySelective(umsMember);
        //删除缓存
        umsMemberCacheService.delMember(umsMember.getId());
    }

    /**
     * s刷新token
     * @param token
     * @return
     */
    @Override
    public String refreshToken(String token) {
        return jwtTokenUtil.refreshHeadToken(token);
    }

    /**
     * 根据会员id修改会员积分
     * @param id
     * @param integration
     */
    @Override
    public void updateIntegration(Long id, Integer integration) {
        UmsMember umsMember = new UmsMember();
        umsMember.setId(id);
        umsMember.setIntegration(integration);
        umsMemberMapper.updateByPrimaryKeySelective(umsMember);
        //删除缓存
        umsMemberCacheService.delMember(umsMember.getId());
    }

    /**
     * 根据用户名获取会员
     * @param username
     * @return
     */
    @Override
    public UmsMember getByUsername(String username) {
        //查缓存
        UmsMember member = umsMemberCacheService.getMember(username);
        if(member!=null){
            return member;
        }
        //查数据库
        UmsMemberExample example = new UmsMemberExample();
        example.createCriteria().andUsernameEqualTo(username);
        List<UmsMember> memberList = umsMemberMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(memberList)) {
            member = memberList.get(0);
            //更新到缓存
            umsMemberCacheService.setMember(member);
            return member;
        }
        return null;
    }

    /**
     * 根据会员id获取会员
     * @param id
     * @return
     */
    @Override
    public UmsMember getById(Long id) {
        return umsMemberMapper.selectByPrimaryKey(id);
    }
}
