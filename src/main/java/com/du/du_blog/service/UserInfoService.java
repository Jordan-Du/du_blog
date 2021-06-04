package com.du.du_blog.service;

import com.du.du_blog.dto.PageDTO;
import com.du.du_blog.dto.UserOnlineDTO;
import com.du.du_blog.vo.ConditionVO;
import com.du.du_blog.vo.EmailVO;
import com.du.du_blog.vo.UserInfoVO;
import com.du.du_blog.vo.UserRoleVO;
import org.springframework.web.multipart.MultipartFile;

public interface UserInfoService {
    /**
     * 修改用户资料
     *
     * @param userInfoVO 用户资料
     */
    void updateUserInfo(
            UserInfoVO userInfoVO);

    /**
     * 修改用户头像
     *
     * @param file 头像图片
     * @return 头像OSS地址
     */
    String updateUserAvatar(MultipartFile file);

    /**
     * 绑定用户邮箱
     * @param emailVO 邮箱
     */
    void saveUserEmail(EmailVO emailVO);

    /**
     * 修改用户权限
     *
     * @param userRoleVO 用户权限
     */
    void updateUserRole(UserRoleVO userRoleVO);

    /**
     * 修改用户禁用状态
     *
     * @param userInfoId 用户信息id
     * @param isDisable  禁用状态
     */
    void updateUserDisable(Integer userInfoId, Integer isDisable);

    /**
     * 查看在线用户列表
     * @param conditionVO 条件
     * @return 在线用户列表
     */
    PageDTO<UserOnlineDTO> listOnlineUsers(ConditionVO conditionVO);

    /**
     * 下线用户
     * @param userInfoId 用户信息id
     */
    void removeOnlineUser(Integer userInfoId);

}
