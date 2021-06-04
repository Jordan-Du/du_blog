package com.du.du_blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.du.du_blog.pojo.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoMapper extends BaseMapper<UserInfo> {

}
