package com.du.du_blog.utils;

import com.du.du_blog.dto.UserInfoDTO;
import com.du.du_blog.pojo.UserAuth;
import com.du.du_blog.pojo.UserInfo;
import com.du.du_blog.vo.UserVO;
import com.sun.org.apache.xpath.internal.operations.Bool;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 用户工具类
 * */
public class UserUtils {
    /*
    * 封装UserDetails
    * */

    public static UserInfoDTO setUserDetails(UserAuth userAuth, UserInfo userInfo, List<String> roles, Set<Integer> articleLikeSet,Set<Integer> commentLikeSet, HttpServletRequest request){
        String ipAddr = IpUtils.getIpAddr(request);
        String ipSource = IpUtils.getIpSource(ipAddr);
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        UserInfoDTO userInfoDTO = UserInfoDTO.builder()
                .id(userAuth.getId())
                .userInfoId(userInfo.getId())
                .username(userAuth.getUsername())
                .email(userInfo.getEmail())
                .loginType(userAuth.getLoginType())
                .password(userAuth.getPassword())
                .nickname(userInfo.getNickname())
                .roleList(roles)
                .avatar(userInfo.getAvatar())
                .webSite(userInfo.getWebSite())
                .articleLikeSet(articleLikeSet)
                .commentLikeSet(commentLikeSet)
                .ipAddr(ipAddr)
                .ipSource(ipSource)
                .browser(userAgent.getBrowser().getName())
                .os(userAgent.getOperatingSystem().getName())
                .lastLoginTime(new Date())
                .build();
        return userInfoDTO;
    }

    /*
    * 获取登录用户UserDetails
    * */
    public static UserInfoDTO getLoginUser(){
        return (UserInfoDTO)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }


}
