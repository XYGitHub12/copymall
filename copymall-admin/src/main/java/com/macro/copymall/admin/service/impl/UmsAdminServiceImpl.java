package com.macro.copymall.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.macro.copymall.admin.bo.AdminUserDetails;
import com.macro.copymall.admin.dao.UmsAdminRoleRelationDao;
import com.macro.copymall.admin.model.UmsAdminParam;
import com.macro.copymall.admin.model.UpdateAdminPasswordParam;
import com.macro.copymall.admin.service.UmsAdminCacheService;
import com.macro.copymall.admin.service.UmsAdminService;
import com.macro.copymall.mbg.mapper.UmsAdminLoginLogMapper;
import com.macro.copymall.mbg.mapper.UmsAdminMapper;
import com.macro.copymall.mbg.mapper.UmsAdminPermissionRelationMapper;
import com.macro.copymall.mbg.mapper.UmsAdminRoleRelationMapper;
import com.macro.copymall.mbg.model.*;
import com.macro.copymall.security.util.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 员后台管理Service实现类
 */
@Service
public class UmsAdminServiceImpl implements UmsAdminService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UmsAdminServiceImpl.class);

    @Autowired
    private UmsAdminMapper adminMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UmsAdminRoleRelationMapper adminRoleRelationMapper;
    @Autowired
    private UmsAdminRoleRelationDao adminRoleRelationDao;
    @Autowired
    private UmsAdminPermissionRelationMapper adminPermissionRelationMapper;
    @Autowired
    private UmsAdminLoginLogMapper adminLoginLogMapper;
    @Autowired
    private UmsAdminCacheService adminCacheService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    //用户注册
    @Override
    public UmsAdmin register(UmsAdminParam umsAdminParam) {
        UmsAdmin admin = new UmsAdmin();
        BeanUtils.copyProperties(umsAdminParam,admin);
        admin.setCreateTime(new Date());
        admin.setStatus(1);
        //查询是否有相同用户名的用户
        UmsAdminExample umsAdminExample = new UmsAdminExample();
        umsAdminExample.createCriteria().andUsernameEqualTo(admin.getUsername());
        List<UmsAdmin> adminList = adminMapper.selectByExample(umsAdminExample);
        if (adminList.size()>0){
            return null;
        }
        //将密码加密
        String encodePassword = passwordEncoder.encode(admin.getPassword());
        admin.setPassword(encodePassword);
        //存入数据库
        adminMapper.insert(admin);
        return admin;
    }

    //用户登录并返回token
    @Override
    public String login(String username, String password) {
        String token = null;
        try {
            //获取用户详情
            AdminUserDetails userDetails = loadUserByUsername(username);
            //判断密码是否匹配
            if (!passwordEncoder.matches(password,userDetails.getPassword())){
                throw new BadCredentialsException("密码不正确");
            }
            //认证
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities());
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


    //通过用户名获取用户基本信息
    public UmsAdmin getAdminByUsername(String username) {
        //去缓存中查
        UmsAdmin admin = adminCacheService.getAdmin(username);
        if (admin != null){
            return admin;
        }
        //缓存中没有就去数据库查
        UmsAdminExample umsAdminExample = new UmsAdminExample();
        umsAdminExample.createCriteria().andUsernameEqualTo(username);
        List<UmsAdmin> adminList = adminMapper.selectByExample(umsAdminExample);
        if (adminList != null && adminList.size()>0){
            admin = adminList.get(0);
            //存到缓存中
            adminCacheService.setAdmin(admin);
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
        adminLoginLogMapper.insert(loginLog);
    }

    //刷新token
    @Override
    public String refreshToken(String oldToken) {
        return jwtTokenUtil.refreshHeadToken(oldToken);
    }

    //根据用户id获取用户
    @Override
    public UmsAdmin getItem(Long id) {
        return adminMapper.selectByPrimaryKey(id);
    }

    //根据用户名或昵称分页查询用户
    @Override
    public List<UmsAdmin> list(String keyword, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageSize,pageNum);
        UmsAdminExample adminExample = new UmsAdminExample();
        UmsAdminExample.Criteria criteria = adminExample.createCriteria();
        if (!StringUtils.isEmpty(keyword)){
            criteria.andUsernameLike("%"+keyword+"%");
            adminExample.or(adminExample.createCriteria().andNickNameLike("%"+keyword+"%"));
        }
        return adminMapper.selectByExample(adminExample);
    }

    //修改指定用户信息
    @Override
    public int update(Long id, UmsAdmin admin) {
        admin.setId(id);
        UmsAdmin oldAdmin = adminMapper.selectByPrimaryKey(id);
        if (admin.getPassword().equals(oldAdmin.getPassword())){
            //修改信息时要判断密码，因为密码需要加密；密码相同，不需要修改。
            admin.setPassword(null);
        }else {
            if (StrUtil.isEmpty(admin.getPassword())){
                admin.setPassword(null);
            }else {
                admin.setPassword(passwordEncoder.encode(admin.getPassword()));
            }
        }
        int count = adminMapper.updateByPrimaryKeySelective(admin);
        //删除缓存
        adminCacheService.delAdmin(id);
        return count;
    }

    //删除指定用户
    @Override
    public int delete(Long id) {
        adminCacheService.delAdmin(id);
        int count = adminMapper.deleteByPrimaryKey(id);
        adminCacheService.delResourceList(id);
        return count;
    }

    //修改用户角色关系
    @Override
    public int updateRole(Long adminId, List<Long> roleIds) {
        int count = roleIds ==null ? 0 : roleIds.size();
        //删除原来关系
        UmsAdminRoleRelationExample adminRoleRelationExample = new UmsAdminRoleRelationExample();
        adminRoleRelationExample.createCriteria().andAdminIdEqualTo(adminId);
        adminRoleRelationMapper.deleteByExample(adminRoleRelationExample);
        //建立新关系
        if (!CollectionUtils.isEmpty(roleIds)){
            List<UmsAdminRoleRelation> list = new ArrayList<>();
            for (long roleId : roleIds){
                UmsAdminRoleRelation adminRoleRelation = new UmsAdminRoleRelation();
                adminRoleRelation.setAdminId(adminId);
                adminRoleRelation.setRoleId(roleId);
                list.add(adminRoleRelation);
            }
            adminRoleRelationDao.insertList(list);
        }
        //删除缓存
        adminCacheService.delResourceList(adminId);
        return count;
    }

    //获取用户的角色
    @Override
    public List<UmsResource> getRoleList(Long adminId) {
        List<UmsResource> roleList = adminRoleRelationDao.getRoleList(adminId);
        return roleList;
    }

    //获取指定用户的可访问资源
    @Override
    public List<UmsResource> getResourceList(Long adminId) {
        List<UmsResource> resourceList = adminCacheService.getResourceList(adminId);
        if (CollUtil.isNotEmpty(resourceList)){
            return resourceList;
        }
        //缓存没有，查数据库
        resourceList = adminRoleRelationDao.getResourceList(adminId);
        if (CollUtil.isNotEmpty(resourceList)){
            //添加到缓存
            adminCacheService.setResourceList(adminId,resourceList);
        }
        return resourceList;
    }

    //修改用户的+-权限
    @Override
    public int updatePermission(Long adminId, List<Long> permissionIds) {
//        //删除原有的权限
//        UmsAdminPermissionRelationExample adminPermissionRelationExample = new UmsAdminPermissionRelationExample();
//        adminPermissionRelationExample.createCriteria().andAdminIdEqualTo(adminId);
//        adminPermissionRelationMapper.deleteByExample(adminPermissionRelationExample);
//        //获取用户所有角色的权限
//        List<UmsAdminPermissionRelation> permissionList = adminRoleRelationDao.getRolePermissionList(adminId);
//        List<Long> rolePermissionList = permissionList.stream().map(UmsPermission::getId).collect(Collectors.toList());
//        if (!CollectionUtils.isEmpty(permissionIds)){
//            List<UmsAdminPermissionRelation> relationList = new ArrayList<>();
//            //筛选出+权限
//            List<Long> addPermissionIdList = permissionIds.stream().filter(permissionId -> !rolePermissionList.contains(permissionId)).collect(Collectors.toList());
//            //筛选出-权限
//            List<Long> subPermissionIdList = rolePermissionList.stream().filter(permissionId -> !permissionIds.contains(permissionId)).collect(Collectors.toList());
//            //插入+-权限关系
//            relationList.addAll(convert(adminId,1,addPermissionIdList));
//            relationList.addAll(convert(adminId,-1,subPermissionIdList));
//            return adminPermissionRelationDao.insertList(relationList);
//        }
        return 0;
    }

    /**
     * 将+-权限关系转化为对象
     */
    private List<UmsAdminPermissionRelation> convert(Long adminId,Integer type,List<Long> permissionIdList) {
        List<UmsAdminPermissionRelation> relationList = permissionIdList.stream().map(permissionId -> {
            UmsAdminPermissionRelation relation = new UmsAdminPermissionRelation();
            relation.setAdminId(adminId);
            relation.setType(type);
            relation.setPermissionId(permissionId);
            return relation;
        }).collect(Collectors.toList());
        return relationList;
    }


    @Override
    public List<UmsPermission> getPermissionList(Long adminId) {
        return null;
    }

    //修改密码
    @Override
    public int updatePassword(UpdateAdminPasswordParam param) {
        if (StrUtil.isEmpty(param.getUsername())
                || (StrUtil.isEmpty(param.getOldPassword())
                || (StrUtil.isEmpty(param.getNewPassword())))){
            return -1;
        }
        UmsAdminExample adminExample = new UmsAdminExample();
        adminExample.createCriteria().andUsernameEqualTo(param.getUsername());
        List<UmsAdmin> adminList = adminMapper.selectByExample(adminExample);
        if (CollUtil.isEmpty(adminList)){
            return -2;
        }
        UmsAdmin admin = adminList.get(0);
        if (!passwordEncoder.matches(param.getOldPassword(),admin.getPassword())){
            return -3;
        }
        admin.setPassword(passwordEncoder.encode(param.getNewPassword()));
        adminMapper.updateByPrimaryKey(admin);
        //删除缓存
        adminCacheService.delAdmin(admin.getId());
        return 1;
    }

    //修改账号状态
    @Override
    public int updateStatus(Long id, UmsAdmin admin) {
        admin.setId(id);
        int count = adminMapper.updateByPrimaryKeySelective(admin);
        adminCacheService.delAdmin(id);
        return count;
    }


}
