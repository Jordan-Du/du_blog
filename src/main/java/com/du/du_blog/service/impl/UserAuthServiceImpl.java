package com.du.du_blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.du.du_blog.constant.CommonConst;
import com.du.du_blog.constant.RabbitMQPreFixConst;
import com.du.du_blog.constant.RedisPreFixConst;
import com.du.du_blog.dao.UserAuthMapper;
import com.du.du_blog.dao.UserInfoMapper;
import com.du.du_blog.dao.UserRoleMapper;
import com.du.du_blog.dto.EmailDTO;
import com.du.du_blog.dto.PageDTO;
import com.du.du_blog.dto.UserBackDTO;
import com.du.du_blog.dto.UserInfoDTO;
import com.du.du_blog.enums.LoginTypeEnum;
import com.du.du_blog.enums.RoleEnum;
import com.du.du_blog.exception.ServerException;
import com.du.du_blog.pojo.UserAuth;
import com.du.du_blog.pojo.UserInfo;
import com.du.du_blog.pojo.UserRole;
import com.du.du_blog.service.UserAuthService;
import com.du.du_blog.utils.UserUtils;
import com.du.du_blog.vo.ConditionVO;
import com.du.du_blog.vo.PasswordVO;
import com.du.du_blog.vo.UserVO;
import net.bytebuddy.asm.Advice;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserAuthServiceImpl extends ServiceImpl<UserAuthMapper, UserAuth> implements UserAuthService {


    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private UserAuthMapper userAuthMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public void sendCode(String username) {
        //????????????
        if(!checkEmail(username)){
            throw new ServerException("???????????????????????????");
        }
        //???????????????
        StringBuffer code = new StringBuffer();
        Random random = new Random();
        for(int i=0;i<6;i++){
            code.append(random.nextInt(10));
        }
        EmailDTO emailDTO = EmailDTO.builder()
                .email(username)
                .subject("??????du_blog????????????")
                .content("?????????????????????" + code.toString() + "????????????10??????!")
                .build();
        //??????email???????????????
        rabbitTemplate.convertAndSend(RabbitMQPreFixConst.EMAIL_EXCHANGE,"", JSON.toJSONBytes(emailDTO));
        //??????????????????redis???
        redisTemplate.boundValueOps(RedisPreFixConst.CODE_KEY+username).set(code.toString());
        //??????????????????
        redisTemplate.expire(RedisPreFixConst.CODE_KEY+username,10*60*1000, TimeUnit.MILLISECONDS);


    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void registerUser(UserVO user) {
        if(checkUser(user)){
            throw new ServerException("??????");
        }
        UserInfo userInfo = UserInfo.builder()
                .email(user.getUsername())
                .nickname(CommonConst.DEFAULT_NICKNAME)
                .avatar(CommonConst.DEFAULT_AVATAR)
                .build();
        userInfoMapper.insert(userInfo);
        saveUserRole(userInfo);
        UserAuth userAuth = UserAuth.builder()
                .userInfoId(userInfo.getId())
                .username(user.getUsername())
                .password(new BCryptPasswordEncoder().encode(user.getPassword()))
                .loginType(LoginTypeEnum.EMAIL.getType())
                .build();
        userAuthMapper.insert(userAuth);

    }

    @Override
    public UserInfoDTO qqLogin(String openId, String accessToken) {
        return null;
    }

    @Override
    public UserInfoDTO weiBoLogin(String code) {
        return null;
    }

    @Override
    public void updatePassword(UserVO user) {
        //????????????????????????
        if(!checkUser(user)){
            throw new ServerException("?????????????????????");
        }
        //??????????????????
        userAuthMapper.update(new UserAuth(),new LambdaUpdateWrapper<UserAuth>()
        .set(UserAuth::getPassword,new BCryptPasswordEncoder().encode(user.getPassword()))
        .eq(UserAuth::getPassword,user.getUsername()));

    }

    @Override
    public void updateAdminPassword(PasswordVO passwordVO) {
        //?????????????????????
        UserAuth userAuth = userAuthMapper.selectById(UserUtils.getLoginUser().getId());
        //????????????????????????
        if(null!=userAuth&& BCrypt.checkpw(passwordVO.getOldPassword(),userAuth.getPassword())){
            userAuthMapper.update(new UserAuth(),new LambdaUpdateWrapper<UserAuth>()
            .set(UserAuth::getPassword,new BCryptPasswordEncoder().encode(passwordVO.getNewPassword()))
            .eq(UserAuth::getId,userAuth.getId()));
        }else{
            throw new ServerException("???????????????");
        }
    }

    @Override
    public PageDTO<UserBackDTO> listUserBackDTO(ConditionVO condition) {
        condition.setCurrent((condition.getCurrent()-1)*condition.getSize());
        Integer count = userInfoMapper.selectCount(new LambdaQueryWrapper<UserInfo>()
                .like(Objects.nonNull(condition.getKeywords()), UserInfo::getNickname, condition.getKeywords()));
        List<UserBackDTO> userBackDTOS = userAuthMapper.selectUsers(condition);
        if(userBackDTOS.isEmpty()){
            return new PageDTO<>();
        }
        return new PageDTO<>(count,userBackDTOS);
    }

    /**
     * ??????????????????
     * @param userVO
     * @return
     */
    public  Boolean checkUser(UserVO userVO){
        if(!userVO.getCode().equals(redisTemplate.boundValueOps(RedisPreFixConst.CODE_KEY+userVO.getUsername()).get())){
            throw new ServerException("??????????????????");
        }
        UserAuth userAuth = userAuthMapper.selectByUserName(userVO.getUsername());
        return (userAuth!=null);
    }

    /**
     * ??????????????????
     * @param username
     * @return
     */
    public static boolean checkEmail(String username) {
        //???????????????
        String rule = "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$";
        //???????????????????????? ?????????????????????
        Pattern p = Pattern.compile(rule);
        //???????????????????????????
        Matcher m = p.matcher(username);
        //??????????????????
        return m.matches();
    }

    /**
     * ????????????
     * @param userInfo
     */
    public void saveUserRole(UserInfo userInfo){
        UserRole userRole = UserRole.builder()
                .userId(userInfo.getId())
                .roleId(RoleEnum.USER.getRoleId())
                .build();
        userRoleMapper.insert(userRole);
    }

}
