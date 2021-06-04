package com.du.du_blog.service.impl;

import com.du.du_blog.constant.RedisPreFixConst;
import com.du.du_blog.utils.IpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static com.du.du_blog.constant.RedisPreFixConst.IP_SET;

@Component
public class ServletRequestListenerImpl implements ServletRequestListener {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        HttpServletRequest request = (HttpServletRequest) sre.getServletRequest();
        HttpSession session = request.getSession();
        String ip = (String)session.getAttribute("ip");
        if(ObjectUtils.isEmpty(ip)){
            String ipAddr = IpUtils.getIpAddr(request);
            session.setAttribute("ip",ipAddr);
            redisTemplate.boundValueOps(RedisPreFixConst.BLOG_VIEWS_COUNT).increment(1);
            redisTemplate.boundSetOps(IP_SET).add(ipAddr);
        }
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void clean(){
        redisTemplate.delete(IP_SET);
    }
}
