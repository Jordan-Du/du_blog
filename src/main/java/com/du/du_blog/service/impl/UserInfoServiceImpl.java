package com.du.du_blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.du.du_blog.constant.RedisPreFixConst;
import com.du.du_blog.dao.UserInfoMapper;
import com.du.du_blog.dao.UserRoleMapper;
import com.du.du_blog.dto.PageDTO;
import com.du.du_blog.dto.UserInfoDTO;
import com.du.du_blog.dto.UserOnlineDTO;
import com.du.du_blog.exception.ServerException;
import com.du.du_blog.pojo.UserInfo;
import com.du.du_blog.pojo.UserRole;
import com.du.du_blog.service.UserInfoService;
import com.du.du_blog.service.UserRoleService;
import com.du.du_blog.utils.BeanCopyUtil;
import com.du.du_blog.utils.UserUtils;
import com.du.du_blog.vo.ConditionVO;
import com.du.du_blog.vo.EmailVO;
import com.du.du_blog.vo.UserInfoVO;
import com.du.du_blog.vo.UserRoleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sun.plugin.com.event.COMEventHandler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service

public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserRoleServiceImpl userRoleService;
    @Autowired
    private SessionRegistry sessionRegistry;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateUserInfo(UserInfoVO userInfoVO) {
        UserInfo userInfo = UserInfo.builder()
                .id(UserUtils.getLoginUser().getId())
                .nickname(userInfoVO.getNickname())
                .intro(userInfoVO.getIntro())
                .webSite(userInfoVO.getWebSite())
                .build();
        this.updateById(userInfo);
    }

    @Override
    public String updateUserAvatar(MultipartFile file) {
        return null;
    }

    @Override
    public void saveUserEmail(EmailVO emailVO) {
        if (!emailVO.getCode().equals(redisTemplate.boundValueOps(RedisPreFixConst.CODE_KEY + emailVO.getEmail()).get())) {
            throw new ServerException("验证码错误！");
        }
        UserInfo build = UserInfo.builder()
                .id(UserUtils.getLoginUser().getId())
                .email(emailVO.getEmail())
                .build();
        this.updateById(build);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateUserRole(UserRoleVO userRoleVO) {
        UserInfo build = UserInfo.builder()
                .id(userRoleVO.getUserInfoId())
                .nickname(userRoleVO.getNickname())
                .build();
        this.updateById(build);
        //删除原用户角色映射
        userRoleService.remove(new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId,userRoleVO.getUserInfoId()));
        List<UserRole> userRoles = userRoleVO.getRoleIdList().stream()
                .map(roleId -> UserRole.builder()
                        .userId(userRoleVO.getUserInfoId())
                        .roleId(roleId)
                        .build())
                .collect(Collectors.toList());
        //更新用户角色映射
        userRoleService.saveBatch(userRoles);

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateUserDisable(Integer userInfoId, Integer isDisable) {
        UserInfo build = UserInfo.builder()
                .id(userInfoId)
                .isDisable(isDisable)
                .build();
        this.updateById(build);
    }

    @Override
    public PageDTO<UserOnlineDTO> listOnlineUsers(ConditionVO conditionVO) {
        //获得在线用户
        List<UserOnlineDTO> userOnlineDTOS = sessionRegistry.getAllPrincipals().stream()
                .filter(item -> sessionRegistry.getAllSessions(item, false).size() > 0)
                .map(item -> BeanCopyUtil.copyObject(item, UserOnlineDTO.class))
                .sorted(Comparator.comparing(UserOnlineDTO::getLastLoginTime).reversed())
                .collect(Collectors.toList());
        //对结果进行分页
        int current=(conditionVO.getCurrent()-1)*conditionVO.getSize();
        int size=userOnlineDTOS.size()>current+conditionVO.getSize()?current+conditionVO.getSize():userOnlineDTOS.size();
        List<UserOnlineDTO> onlineDTOS = userOnlineDTOS.subList(current, current + size);
        return new PageDTO(userOnlineDTOS.size(),onlineDTOS);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeOnlineUser(Integer userInfoId) {
        //获得用户session
        List<Object> objectList = sessionRegistry.getAllPrincipals().stream()
                .filter(item -> {
                    UserInfoDTO userInfoDTO = (UserInfoDTO) item;
                    return userInfoDTO.getUserInfoId().intValue() == userInfoId.intValue();
                }).collect(Collectors.toList());
        List<SessionInformation> allSessions=new ArrayList<>();
        objectList.forEach(item->allSessions.addAll(sessionRegistry.getAllSessions(item,false)));
        //注销使用指定session
        allSessions.forEach(SessionInformation::expireNow);
    }
}
