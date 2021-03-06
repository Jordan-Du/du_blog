package com.du.du_blog.filter;

import com.alibaba.fastjson.JSON;
import com.du.du_blog.constant.RedisPreFixConst;
import com.du.du_blog.constant.StatusConst;
import com.du.du_blog.dao.RoleMapper;
import com.du.du_blog.dao.UserAuthMapper;
import com.du.du_blog.dao.UserInfoMapper;
import com.du.du_blog.dto.UserInfoDTO;
import com.du.du_blog.pojo.UserAuth;
import com.du.du_blog.pojo.UserInfo;
import com.du.du_blog.utils.JWTUtils;
import com.du.du_blog.utils.UserUtils;
import com.du.du_blog.vo.Result;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.ObjectUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.rmi.ServerException;
import java.util.List;
import java.util.Set;

public class JWTFilter extends BasicAuthenticationFilter {

    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private UserAuthMapper userAuthMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    public JWTFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        //??????????????????token
        String jwt = request.getHeader(jwtUtils.getHeader());
        //token?????????????????????????????????
        if(jwt==null||jwt==""){
            chain.doFilter(request,response);
            return;
        }
        Claims claims = jwtUtils.getClaimsByToken(jwt);
        //??????token??????
        if(claims==null){
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(JSON.toJSONString(new Result(false, StatusConst.TOKEN_ERROR, "token?????????")));
            return;
        }
        if(jwtUtils.isTokenExpired(claims)){
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(JSON.toJSONString(new Result(false, StatusConst.TOKEN_EXPIRED, "???????????????????????????????????????")));
            return;
        }
        String username = claims.getSubject();
        //??????????????????
        UserAuth userAuth = userAuthMapper.selectByUserName(username);
        if (userAuth == null) {
            throw new UsernameNotFoundException("??????????????????");
        }
        UserInfo userInfo = userInfoMapper.selectById(userAuth.getUserInfoId());
        List<String> roles = roleMapper.listRolesByUserInfoId(userInfo.getId());

        /**
         * ----------------------??????????????????????????????????????????------------------------
         */
        Set<Integer> articleLikeSet = (Set<Integer>) redisTemplate.boundHashOps(RedisPreFixConst.ARTICLE_USER_LIKE).get(userInfo.getId().toString());
        Set<Integer> commentLikeSet = (Set<Integer>) redisTemplate.boundHashOps(RedisPreFixConst.COMMENT_USER_LIKE).get(userInfo.getId().toString());
        UserInfoDTO userDetails = UserUtils.setUserDetails(userAuth, userInfo, roles, articleLikeSet, commentLikeSet, request);
        //?????????????????????????????????
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        chain.doFilter(request,response);
    }

}
