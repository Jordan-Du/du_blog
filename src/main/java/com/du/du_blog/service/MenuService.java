package com.du.du_blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.du.du_blog.dto.MenuDTO;
import com.du.du_blog.dto.UserMenuDTO;
import com.du.du_blog.dto.labelOptionDTO;
import com.du.du_blog.pojo.Menu;
import com.du.du_blog.vo.ConditionVO;

import java.util.List;

/**
 * @author: yezhiqiu
 * @date: 2021-01-23
 **/
public interface MenuService extends IService<Menu> {

    /**
     * 查看菜单列表
     * @param conditionVO 条件
     * @return 菜单列表
     */
    List<MenuDTO> listMenus();

    /**
     * 查看角色菜单选项
     * @return 角色菜单选项
     */
    List<labelOptionDTO> listMenuOptions();

    /**
     * 查看用户菜单
     * @return 菜单列表
     */
    List<UserMenuDTO> listUserMenus();

}
