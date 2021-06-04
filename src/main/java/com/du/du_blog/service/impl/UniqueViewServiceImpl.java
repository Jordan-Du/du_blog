package com.du.du_blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.du.du_blog.dao.UniqueViewMapper;
import com.du.du_blog.dto.UniqueViewDTO;
import com.du.du_blog.pojo.UniqueView;
import com.du.du_blog.service.UniqueViewService;
import com.du.du_blog.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.du.du_blog.constant.RedisPreFixConst.IP_SET;

@Service
public class UniqueViewServiceImpl extends ServiceImpl<UniqueViewMapper, UniqueView> implements UniqueViewService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UniqueViewMapper uniqueViewMapper;

    @Scheduled(cron = "0 0 1 * * ?")
    @Override
    public void saveUniqueView() {
        //获得每日用户量
        Long count = redisTemplate.boundSetOps(IP_SET).size();
        UniqueView uniqueView = UniqueView.builder()
                //时间设置为前一天
                .createTime(DateUtils.getSomeDay(new Date(), -1))
                .viewsCount(null != count ? count.intValue() : 0)
                .build();
        uniqueViewMapper.insert(uniqueView);
    }

    @Override
    public List<UniqueViewDTO> listUniqueViews() {
        String startTime = DateUtils.getMinTime(DateUtils.getSomeDay(new Date(), -7));
        String endTime = DateUtils.getMaxTime(new Date());
        return uniqueViewMapper.listUniqueViews(startTime, endTime);
    }
}
