package com.du.du_blog.handler;

import com.alibaba.fastjson.JSON;
import com.du.du_blog.constant.StatusConst;
import com.du.du_blog.dao.UserAuthMapper;
import com.du.du_blog.dto.UserInfoDTO;
import com.du.du_blog.pojo.UserAuth;
import com.du.du_blog.utils.UserUtils;
import com.du.du_blog.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthenticationFailureHandlerImpl implements AuthenticationFailureHandler {


    /*
    * 登录失败时，返回错误信息
    * */
    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json;charset=utf-8");
        httpServletResponse.getWriter().write(JSON.toJSONString(new Result(false, StatusConst.ERROR,e.getMessage())));
    }



}
