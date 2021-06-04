package com.du.du_blog.service.impl;

import com.du.du_blog.constant.RedisPreFixConst;
import com.du.du_blog.dao.RoleMapper;
import com.du.du_blog.dao.UserAuthMapper;
import com.du.du_blog.dao.UserInfoMapper;
import com.du.du_blog.dao.UserRoleMapper;
import com.du.du_blog.pojo.Role;
import com.du.du_blog.pojo.UserAuth;
import com.du.du_blog.pojo.UserInfo;
import com.du.du_blog.pojo.UserRole;
import com.du.du_blog.utils.UserUtils;
import jdk.nashorn.internal.ir.CallNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.ServerEndpoint;
import java.rmi.ServerException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserAuthMapper userAuthMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Resource
    private HttpServletRequest request;

    @Override
    public UserDetails loadUserByUsername(String username) {
        UserAuth userAuth = userAuthMapper.selectByUserName(username);
        if (userAuth == null) {
            throw new UsernameNotFoundException("用户不存在！");
        }
        UserInfo userInfo = userInfoMapper.selectById(userAuth.getUserInfoId());
        List<String> roles = roleMapper.listRolesByUserInfoId(userInfo.getId());

        /**
         * ----------------------用户的文章点赞和留言点赞集合------------------------
         */
        Set<Integer> articleLikeSet = (Set<Integer>) redisTemplate.boundHashOps(RedisPreFixConst.ARTICLE_USER_LIKE).get(userInfo.getId().toString());
        Set<Integer> commentLikeSet = (Set<Integer>) redisTemplate.boundHashOps(RedisPreFixConst.COMMENT_USER_LIKE).get(userInfo.getId().toString());
        return UserUtils.setUserDetails(userAuth, userInfo, roles, articleLikeSet, commentLikeSet, request);
    }
}
