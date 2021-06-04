package com.du.du_blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.du.du_blog.dao.RoleMenuMapper;
import com.du.du_blog.pojo.RoleMenu;
import com.du.du_blog.service.RoleMenuService;
import org.springframework.stereotype.Service;

@Service
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements RoleMenuService {
}
