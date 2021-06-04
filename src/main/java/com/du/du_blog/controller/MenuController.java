package com.du.du_blog.controller;

import com.du.du_blog.constant.StatusConst;
import com.du.du_blog.dto.MenuDTO;
import com.du.du_blog.dto.UserMenuDTO;
import com.du.du_blog.dto.labelOptionDTO;
import com.du.du_blog.service.MenuService;
import com.du.du_blog.vo.ConditionVO;
import com.du.du_blog.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: yezhiqiu
 * @date: 2021-01-23
 **/
@Api(tags = "菜单模块")
@RestController
public class MenuController {
    @Autowired
    private MenuService menuService;

    @ApiOperation(value = "查看菜单列表")
    @GetMapping("/admin/menus")
    public Result<List<MenuDTO>> listMenus() {
        return new Result<>(true, StatusConst.OK, "查询成功", menuService.listMenus());
    }

    @ApiOperation(value = "查看菜单选项列表")
    @GetMapping("/admin/role/menus")
    public Result<List<labelOptionDTO>> listMenuOptions() {
        return new Result<>(true, StatusConst.OK, "查询成功", menuService.listMenuOptions());
    }

    @ApiOperation(value = "查看用户菜单列表")
    @GetMapping("/admin/user/menus")
    public Result<List<UserMenuDTO>> listUserMenus() {
        return new Result<>(true, StatusConst.OK, "查询成功", menuService.listUserMenus());
    }

}
