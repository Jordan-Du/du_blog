package com.du.du_blog.service;

import com.du.du_blog.dto.BlogBackInfoDTO;
import com.du.du_blog.dto.BlogHomeInfoDTO;

public interface BlogInfoService  {

    /**
     * 获取首页数据
     * @return 博客首页信息
     */
    BlogHomeInfoDTO getBlogInfo();

    /**
     * 获取后台首页数据
     * @return 博客后台信息
     */
    BlogBackInfoDTO getBlogBackInfo();

    /**
     * 获取关于我内容
     * @return 关于我内容
     */
    String getAbout();

    /**
     * 修改关于我内容
     * @param aboutContent 关于我内容
     */
    void saveOrUpdateAbout(String aboutContent);

    /**
     * 修改公告
     * @param notice 公告
     */
    void saveOrUpdateNotice(String notice);

    /**
     * 后台查看公告
     * @return 公告
     */
    String getNotice();

}
