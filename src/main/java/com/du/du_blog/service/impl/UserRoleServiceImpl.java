package com.du.du_blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.du.du_blog.dao.UserRoleMapper;
import com.du.du_blog.pojo.UserRole;
import com.du.du_blog.service.UserRoleService;
import org.springframework.stereotype.Service;

@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {
}
