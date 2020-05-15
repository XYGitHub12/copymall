package com.macro.copymall.admin.service.impl;

import com.macro.copymall.admin.bo.AdminUserDetails;
import com.macro.copymall.admin.dao.UmsAdminRoleRelationDao;
import com.macro.copymall.admin.dto.UmsAdminParam;
import com.macro.copymall.admin.service.UmsAdminCacheService;
import com.macro.copymall.admin.service.UmsAdminService;
import com.macro.copymall.mbg.mapper.UmsAdminLoginLogMapper;
import com.macro.copymall.mbg.mapper.UmsAdminMapper;
import com.macro.copymall.mbg.model.UmsAdmin;
import com.macro.copymall.mbg.model.UmsAdminExample;
import com.macro.copymall.mbg.model.UmsAdminLoginLog;
import com.macro.copymall.mbg.model.UmsResource;
import com.macro.copymall.security.util.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 员后台管理Service实现类
 */
@Service
public class UmsAdminServiceImpl implements UmsAdminService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UmsAdminServiceImpl.class);
    @Autowired
    private UmsAdminMapper umsAdminMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UmsAdminRoleRelationDao adminRoleRelationDao;
    @Autowired
    private UmsAdminLoginLogMapper umsAdminLoginLogMapper;
    @Autowired
    private UmsAdminCacheService umsAdminCacheService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * 用户注册
     * @param umsAdminParam
     * @return
     */
    @Override
    public UmsAdmin register(UmsAdminParam umsAdminParam) {
        UmsAdmin umsAdmin = new UmsAdmin();
        BeanUtils.copyProperties(umsAdminParam,umsAdmin);
        umsAdmin.setCreateTime(new Date());
        umsAdmin.setStatus(1);
        //查询是否有相同用户名的用户
        UmsAdminExample umsAdminExample = new UmsAdminExample();
        umsAdminExample.createCriteria().andUsernameEqualTo(umsAdmin.getUsername());
        List<UmsAdmin> adminList = umsAdminMapper.selectByExample(umsAdminExample);
        if (adminList.size()>0){
            return null;
        }
        //将密码加密
        String encodePassword = passwordEncoder.encode(umsAdmin.getPassword());
        umsAdmin.setPassword(encodePassword);
        //存入数据库
        umsAdminMapper.insert(umsAdmin);
        return umsAdmin;
    }

    /**
     * 用户登录并返回token
     * @param username
     * @param password
     * @return
     */
    @Override
    public String login(String username, String password) {
        String token = null;
        try {
            //获取用户详情
            AdminUserDetails userDetails = loadUserByUsername(username);
            //判断密码(传递的密码密码)
            if (!passwordEncoder.matches(password,userDetails.getPassword())){
                throw new BadCredentialsException("密码不正确");
            }
            //认证
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
            //用来存放authentication(认证成功后保存的访问者信息)
            SecurityContextHolder.getContext().setAuthentication(authentication);
            //生成token
            token = jwtTokenUtil.generateToken(userDetails);
            //添加登录记录
            insertLoginLog(username);
        }catch (AuthenticationException e){
            LOGGER.warn("登录异常:{}",e.getMessage());
        }
        return token;
    }

    /**
     * 添加登录记录
     * @param username
     */
    public void insertLoginLog(String username){
        UmsAdmin umsAdmin = getAdminByUsername(username);
        if (umsAdmin == null){
            return;
        }
        UmsAdminLoginLog loginLog = new UmsAdminLoginLog();
        loginLog.setAdminId(umsAdmin.getId());
        loginLog.setCreateTime(new Date());
        //获取ip地址
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        loginLog.setIp(request.getRemoteAddr());
        umsAdminLoginLogMapper.insert(loginLog);
    }

    /**
     * 通过用户名获取用户基本信息
     * @param username
     * @return
     */
    private UmsAdmin getAdminByUsername(String username) {
        //去缓存中查
        UmsAdmin admin = umsAdminCacheService.getAdmin(username);
        if (admin != null){
            return admin;
        }
        //缓存中没有就去数据库查
        UmsAdminExample umsAdminExample = new UmsAdminExample();
        umsAdminExample.createCriteria().andUsernameEqualTo(username);
        List<UmsAdmin> adminList = umsAdminMapper.selectByExample(umsAdminExample);
        if (adminList != null && adminList.size()>0){
            admin = adminList.get(0);
            //存到缓存中
            umsAdminCacheService.setAdmin(admin);
            return admin;
        }
        return null;
    }

    /**
     * 获取用户详情security需要的
     * @param username
     * @return
     */
    @Override
    public AdminUserDetails loadUserByUsername(String username) {
        //获取用户信息
        UmsAdmin admin = getAdminByUsername(username);
        if (admin != null){
            //获取用户角色
            List<UmsResource> resources = getResourceList(admin.getId());
            //返回security需要的用户详情
            return new AdminUserDetails(admin,resources);
        }
        throw new UsernameNotFoundException("用户名或密码错误");
    }

    /**
     * 获取用户角色
     * @param adminId
     * @return
     */
    private List<UmsResource> getResourceList(Long adminId) {
        //获取用户的所有角色
        return adminRoleRelationDao.getRoleList(adminId);
    }


}
