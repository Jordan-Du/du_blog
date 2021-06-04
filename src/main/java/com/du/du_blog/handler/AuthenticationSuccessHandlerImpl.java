package com.du.du_blog.handler;

import com.alibaba.fastjson.JSON;
import com.du.du_blog.constant.StatusConst;
import com.du.du_blog.dao.UserAuthMapper;
import com.du.du_blog.dto.UserInfoDTO;
import com.du.du_blog.pojo.UserAuth;
import com.du.du_blog.utils.JWTUtils;
import com.du.du_blog.utils.UserUtils;
import com.du.du_blog.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@EnableAsync
@Component
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    @Autowired
    private UserAuthMapper userAuthMapper;

    @Autowired
    private JWTUtils jwtUtils;

    /*
    * 登录成功时，返回JSON数据
    * */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        //更新用户信息
        updateUserAuth();
        UserInfoDTO loginUser = UserUtils.getLoginUser();
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        //创建token
        String token = jwtUtils.createToken(authentication.getName());
        //将token放在请求头中，前端获取
        httpServletResponse.setHeader(jwtUtils.getHeader(),token);
        httpServletResponse.getWriter().write(JSON.toJSONString(new Result<UserInfoDTO>(true, StatusConst.OK,"登录成功",loginUser)));
    }

    /*
    * 更新用户信息
    * */
    @Async
    public void updateUserAuth(){
        UserInfoDTO loginUser = UserUtils.getLoginUser();
        System.out.println(loginUser);
        UserAuth userAuth = UserAuth.builder()
                .id(loginUser.getId())
                .ipAddr(loginUser.getIpAddr())
                .ipSource(loginUser.getIpSource())
                .lastLoginTime(loginUser.getLastLoginTime())
                .loginType(loginUser.getLoginType())
                .build();
        userAuthMapper.updateById(userAuth);
    }
}
