package com.du.du_blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.du.du_blog.pojo.Menu;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MenuMapper extends BaseMapper<Menu> {
    /**
     * 根据用户id查询菜单
     * @param userInfoId 用户信息id
     * @return 菜单列表
     */
    List<Menu> listMenusByUserInfoId(@Param("userInfoId") Integer userInfoId);
}
