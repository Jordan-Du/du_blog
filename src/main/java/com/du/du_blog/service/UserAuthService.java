package com.du.du_blog.service;

import com.du.du_blog.dto.PageDTO;
import com.du.du_blog.dto.UserBackDTO;
import com.du.du_blog.dto.UserInfoDTO;
import com.du.du_blog.vo.ConditionVO;
import com.du.du_blog.vo.PasswordVO;
import com.du.du_blog.vo.UserVO;

public interface UserAuthService {
    /**
     * 发送邮箱验证码
     *
     * @param username 邮箱号
     */
    void sendCode(String username);

    /**
     * 用户注册
     *
     * @param user 用户对象
     */
    void registerUser(UserVO user);

    /**
     * qq登录
     *
     * @param openId      qq openId
     * @param accessToken qq token
     * @return 用户登录信息
     */
    UserInfoDTO qqLogin(String openId, String accessToken);

    /**
     * 微博登录
     *
     * @param code 微博code
     * @return 用户登录信息
     */
    UserInfoDTO weiBoLogin(String code);

    /**
     * 修改密码
     *
     * @param user 用户对象
     */
    void updatePassword(UserVO user);

    /**
     * 修改管理员密码
     *
     * @param passwordVO 密码对象
     */
    void updateAdminPassword(PasswordVO passwordVO);

    /**
     * 查询后台用户列表
     *
     * @param condition 条件
     * @return 用户列表
     */
    PageDTO<UserBackDTO> listUserBackDTO(ConditionVO condition);

}
