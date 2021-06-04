package com.du.du_blog.controller;

import com.du.du_blog.constant.StatusConst;
import com.du.du_blog.dto.PageDTO;
import com.du.du_blog.dto.RoleDTO;
import com.du.du_blog.dto.UserRoleDTO;
import com.du.du_blog.service.RoleService;
import com.du.du_blog.vo.ConditionVO;
import com.du.du_blog.vo.Result;
import com.du.du_blog.vo.RoleVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "角色模块")
@Slf4j
@RestController
public class RoleController {
    @Autowired
    RoleService roleService;

    @ApiOperation(value = "查询用户角色选项")
    @GetMapping("/admin/users/role")
    public Result<List<UserRoleDTO>> listUserRoles(){
        List<UserRoleDTO> userRoleDTOS;
        try {
            userRoleDTOS = roleService.listUserRoles();
            return new Result(true, StatusConst.OK, "查询成功！",userRoleDTOS);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Result(false, StatusConst.ERROR, "系统繁忙！");
        }
    }

    @ApiOperation(value = "查询角色列表")
    @GetMapping("/admin/roles")
    public Result<PageDTO<RoleDTO>> listRoles(ConditionVO conditionVO){
        PageDTO<RoleDTO> roleDTOPageDTO;
        try {
            roleDTOPageDTO = roleService.listRoles(conditionVO);
            return new Result(true, StatusConst.OK, "查询成功！",roleDTOPageDTO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Result(false, StatusConst.ERROR, "系统繁忙！");
        }
    }

    @ApiOperation(value = "保存或更新角色")
    @PostMapping("/admin/role")
    public Result saveOrUpdateRole(@RequestBody RoleVO roleVO){
        try {
            roleService.saveOrUpdateRole(roleVO);
            return new Result(true, StatusConst.OK, "操作成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Result(false, StatusConst.ERROR, e.getMessage());
        }

    }

    @ApiOperation(value = "删除角色")
    @DeleteMapping("/admin/roles")
    public Result deleteRoles(@RequestBody List<Integer> roleIdList){
        try {
            roleService.deleteRoles(roleIdList);
            return new Result(true, StatusConst.OK, "操作成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Result(false, StatusConst.ERROR, e.getMessage());
        }
    }
}
