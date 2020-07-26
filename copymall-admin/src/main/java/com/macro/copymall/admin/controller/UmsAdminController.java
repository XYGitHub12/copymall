package com.macro.copymall.admin.controller;

import com.macro.copymall.admin.model.UmsAdminLoginParam;
import com.macro.copymall.admin.model.UmsAdminParam;
import com.macro.copymall.admin.model.UpdateAdminPasswordParam;
import com.macro.copymall.admin.service.UmsAdminService;
import com.macro.copymall.admin.service.UmsRoleService;
import com.macro.copymall.common.api.CommonPage;
import com.macro.copymall.common.api.CommonResult;
import com.macro.copymall.mbg.model.UmsAdmin;
import com.macro.copymall.mbg.model.UmsPermission;
import com.macro.copymall.mbg.model.UmsRole;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;

/**
 * 后台用户管理Controller
 */
@RestController
@Api(tags = "UmsAdminController",description = "后台用户管理")
@RequestMapping("/admin")
public class UmsAdminController {

    @Autowired
    private UmsAdminService adminService;
    @Autowired
    private UmsRoleService roleService;
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public CommonResult register(@Validated @RequestBody UmsAdminParam adminParam, BindingResult bindingResult){
        UmsAdmin admin = adminService.register(adminParam);
        if (admin == null){
            return CommonResult.failed("注册失败");
        }
        return CommonResult.success(admin);
    }

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public CommonResult login(@RequestBody UmsAdminLoginParam adminLoginParam, BindingResult bindingResult){
        String token = adminService.login(adminLoginParam.getUsername(),adminLoginParam.getPassword());
        if (token == null){
            return CommonResult.failed("用户名或密码错误");
        }
        HashMap<String,String> map = new HashMap<>();
        map.put("token",token);
        map.put("tokenHead",tokenHead);
        return CommonResult.success(map);
    }

    @ApiOperation("刷新token")
    @GetMapping("/refreshToken")
    public CommonResult refreshToken(HttpServletRequest request){
        String token = request.getHeader(tokenHeader);
        String refreshToken = adminService.refreshToken(token);
        if (refreshToken == null){
            return CommonResult.failed("token已经过期");
        }
        HashMap<String,String> map = new HashMap<>();
        map.put("token",refreshToken);
        map.put("tokenHead",tokenHead);
        return CommonResult.success(map);
    }

    @ApiOperation("获取当前登录用户信息")
    @GetMapping("/info")
    public CommonResult info(Principal principal){
        if (principal == null){
            return CommonResult.unauthorized(null);
        }
        String username = principal.getName();
        UmsAdmin admin = adminService.getAdminByUsername(username);
        HashMap<String,Object> map = new HashMap<>();
        map.put("username",admin.getUsername());
        map.put("roles",new String[]{"TEST"});
        map.put("menus",roleService.getMenuList(admin.getId()));
        map.put("icon",admin.getIcon());
        return CommonResult.success(map);
    }

    @ApiOperation("登出")
    @PostMapping("/logout")
    public CommonResult logout(){
        return CommonResult.success(null);
    }

    @ApiOperation("根据用户名或姓名分页获取用户列表")
    @GetMapping("/list")
    public CommonResult<CommonPage<UmsAdmin>> list(@RequestParam(value = "keyword",required = false) String keyword,
                                                   @RequestParam(value = "pageSize",defaultValue = "5") Integer pageSize,
                                                   @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum){
        List<UmsAdmin> adminList = adminService.list(keyword,pageSize,pageNum);
        return CommonResult.success(CommonPage.restPage(adminList));
    }

    @ApiOperation("获取指定用户信息")
    @GetMapping("/{id}")
    public CommonResult<UmsAdmin> getItem(@PathVariable Long id){
        UmsAdmin admin = adminService.getItem(id);
        return CommonResult.success(admin);
    }

    @ApiOperation("修改指定用户信息")
    @PostMapping("/update/{id}")
    public CommonResult update(@PathVariable Long id,@RequestBody UmsAdmin admin){
        int count = adminService.update(id,admin);
        if (count > 0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("修改指定用户密码")
    @PostMapping("/updatePassword")
    public CommonResult updatePassword(@RequestBody UpdateAdminPasswordParam updateAdminPasswordParam){
        int status = adminService.updatePassword(updateAdminPasswordParam);
        if (status > 0){
            return CommonResult.success(status);
        }else if (status == -1){
            return CommonResult.failed("提交参数不合法");
        }else if (status == -2){
            return CommonResult.failed("找不到该用户");
        }else if (status == -3){
            return CommonResult.failed("旧密码错误");
        }else {
            return CommonResult.failed();
        }
    }

    @ApiOperation("删除指定用户信息")
    @PostMapping("/delete/{id}")
    public CommonResult delete(@PathVariable Long id){
        int count = adminService.delete(id);
        if (count > 0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("修改账号状态")
    @PostMapping("/updateStatus/{id}")
    public CommonResult updateStatus(@PathVariable Long id,@RequestParam(value = "status") Integer status){
        UmsAdmin admin = new UmsAdmin();
        admin.setStatus(status);
        int count = adminService.updateStatus(id,admin);
        if (count > 0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("给用户分配角色")
    @PostMapping("/role/update")
    public CommonResult updateRole(@RequestParam(value = "adminId") Long adminId,
                                   @RequestParam(value = "roleIds") List<Long> roleIds){
        int count = adminService.updateRole(adminId,roleIds);
        if (count > 0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("获取指定用户的角色")
    @GetMapping("/role/{adminId}")
    public CommonResult<List<UmsRole>> getRoleList(@PathVariable Long adminId){
        List<UmsRole> roleList = adminService.getRoleList(adminId);
        return CommonResult.success(roleList);
    }

    @ApiOperation("给用户分配+-权限")
    @PostMapping("/permission/update")
    public CommonResult updatePermission(@RequestParam Long adminId,
                                         @RequestParam(value = "permissionIds") List<Long> permissionIds){
        int count = adminService.updatePermission(adminId,permissionIds);
        if (count > 0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("获取用户所有权限（包括+-权限）")
    @GetMapping("/permission/{adminId}")
    public CommonResult<List<UmsPermission>> getPermissionList(@PathVariable Long adminId){
        List<UmsPermission> permissionList = adminService.getPermissionList(adminId);
        return CommonResult.success(permissionList);
    }
}
