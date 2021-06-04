package com.du.du_blog.controller;

import com.du.du_blog.constant.StatusConst;
import com.du.du_blog.dto.PageDTO;
import com.du.du_blog.dto.UserOnlineDTO;
import com.du.du_blog.service.UserInfoService;
import com.du.du_blog.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@Slf4j
@Api(tags = "用户信息模块")
@RestController
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @ApiOperation(value = "修改用户信息")
    @PutMapping("/users/info")
    public Result updateUserInfo(@Valid @RequestBody UserInfoVO userInfoVO) {
        try {
            userInfoService.updateUserInfo(userInfoVO);
            return new Result(true, StatusConst.OK, "修改成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Result(false, StatusConst.ERROR, "系统繁忙！");
        }
    }


    @ApiOperation(value = "修改用户头像")
    @PostMapping("/users/avatar")
    Result<String> updateUserAvatar(MultipartFile file) {
        try {
            String avatar = userInfoService.updateUserAvatar(file);
            return new Result(true, StatusConst.OK, "修改成功！", avatar);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Result(false, StatusConst.ERROR, "系统繁忙！");
        }
    }

    @ApiOperation(value = "绑定用户邮箱")
    @PutMapping("/users/email")
    public Result saveUserEmail(EmailVO emailVO) {
        try {
            userInfoService.saveUserEmail(emailVO);
            return new Result(true, StatusConst.OK, "绑定邮箱成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Result(false, StatusConst.ERROR, "系统繁忙！");
        }
    }


    @ApiOperation(value = "修改用户权限")
    @PutMapping("/admin/users/role")
    public Result updateUserRole(@RequestBody UserRoleVO userRoleVO) {
        try {
            userInfoService.updateUserRole(userRoleVO);
            return new Result(true, StatusConst.OK, "修改成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Result(false, StatusConst.ERROR, "系统繁忙！");
        }
    }


    @ApiOperation(value = "修改用户禁用状态")
    @PutMapping("/admin/users/disable/{userInfoId}")
    public Result updateUserDisable(@PathVariable("userInfoId") Integer userInfoId, Integer isDisable) {
        try {
            userInfoService.updateUserDisable(userInfoId, isDisable);
            return new Result(true, StatusConst.OK, "修改成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Result(false, StatusConst.ERROR, "系统繁忙！");
        }
    }

    @ApiOperation(value = "查看在线用户列表")
    @GetMapping("/admin/users/online")
    public Result<PageDTO<UserOnlineDTO>> listOnlineUsers(ConditionVO conditionVO){
        try {
            PageDTO<UserOnlineDTO> userOnlineDTOPageDTO = userInfoService.listOnlineUsers(conditionVO);
            return new Result(true, StatusConst.OK, "修改成功！",userOnlineDTOPageDTO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Result(false, StatusConst.ERROR, "系统繁忙！");
        }
    }

    @ApiOperation(value = "下线用户")
    @DeleteMapping("/admin/users/online/{userInfoId}")
    public Result removeOnlineUser(@PathVariable("userInfoId") Integer userInfoId){
        try {
            userInfoService.removeOnlineUser(userInfoId);
            return new Result(true, StatusConst.LOGOUT, "下线成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Result(false, StatusConst.ERROR, "系统繁忙！");
        }
    }
}
