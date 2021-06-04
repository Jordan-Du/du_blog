package com.du.du_blog.controller;

import com.du.du_blog.constant.StatusConst;
import com.du.du_blog.dto.PageDTO;
import com.du.du_blog.dto.UserBackDTO;
import com.du.du_blog.service.UserAuthService;
import com.du.du_blog.vo.ConditionVO;
import com.du.du_blog.vo.PasswordVO;
import com.du.du_blog.vo.Result;
import com.du.du_blog.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "用户模块")
@Slf4j
@RestController
public class UserAuthController {

    @Autowired
    private UserAuthService userAuthService;


    @ApiOperation(value = "发送邮箱验证码")
    @ApiImplicitParam(name = "username", value = "用户邮箱", required = true, dataType = "String")
    @GetMapping("/users/code")
    public Result sendCode(String username) {
        try {
            userAuthService.sendCode(username);
            return new Result(true, StatusConst.OK, "发送成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Result(false, StatusConst.ERROR, "系统繁忙！");
        }
    }

    @ApiOperation(value = "使用邮箱注册账户")
    @ApiImplicitParam(name = "user", value = "用户信息", required = true, dataType = "UserVO")
    @PostMapping("/users/register")
    public Result registerUser(@RequestBody UserVO user) {
        try {
            userAuthService.registerUser(user);
            return new Result(true, StatusConst.OK, "注册成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Result(false, StatusConst.ERROR, "系统繁忙！");
        }
    }


    @ApiOperation(value = "获取后台用户列表")
    @GetMapping("/admin/users")
    public Result<PageDTO<UserBackDTO>> listUserBackDTO(ConditionVO condition) {
        PageDTO<UserBackDTO> userBackDTOPageDTO;
        try {
            userBackDTOPageDTO = userAuthService.listUserBackDTO(condition);
            userBackDTOPageDTO.getRecordList().forEach(item-> System.out.println(item));
            return new Result(true, StatusConst.OK, "查询成功！",userBackDTOPageDTO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Result(false, StatusConst.ERROR, "系统繁忙！");
        }
    }
    @ApiOperation(value = "修改用户密码")
    @PutMapping("/users/password")
    public Result updatePassword(@Valid @RequestBody UserVO user){
        try {
            userAuthService.updatePassword(user);
            return new Result(true, StatusConst.OK, "修改成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Result(false, StatusConst.ERROR, "系统繁忙！");
        }
    }
    @ApiOperation(value = "修改管理员密码")
    @PutMapping("/admin/users/password")
    public Result updateAdminPassword(@Valid @RequestBody PasswordVO passwordVO){
        try {
            userAuthService.updateAdminPassword(passwordVO);
            return new Result(true, StatusConst.OK, "修改成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Result(false, StatusConst.ERROR, "系统繁忙！");
        }
    }
}
