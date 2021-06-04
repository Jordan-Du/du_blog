package com.du.du_blog;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.du.du_blog.constant.RabbitMQPreFixConst;
import com.du.du_blog.dao.*;
import com.du.du_blog.dto.*;
import com.du.du_blog.handler.EmailSender;
import com.du.du_blog.pojo.Article;
import com.du.du_blog.pojo.Menu;
import com.du.du_blog.pojo.UserAuth;
import com.du.du_blog.service.MessageService;
import com.du.du_blog.vo.ConditionVO;
import net.minidev.json.JSONUtil;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.AntPathMatcher;
import org.w3c.dom.ls.LSOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
class DuBlogApplicationTests {


    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private UserAuthMapper userAuthMapper;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private EmailSender emailSender;

    @Test
    void contextLoads() {
        List<ArticleHomeDTO> articleHomeDTOS = articleMapper.listArticles((long) ((1 - 1) * 10));
        for (ArticleHomeDTO articleHomeDTO : articleHomeDTOS) {
            System.out.println(articleHomeDTO);
        }

    }

    @Test
    void testCountArticlesBack(){
        ConditionVO conditionVO = new ConditionVO();
        conditionVO.setIsDelete(0);
        conditionVO.setIsDraft(0);
        Integer integer = articleMapper.countArticles(conditionVO);
        System.out.println(integer);
    }

    @Test
    void testListArticlesBack(){
        ConditionVO conditionVO = new ConditionVO();
        conditionVO.setIsDelete(0);
        conditionVO.setIsDraft(0);
        conditionVO.setCurrent(0);
        conditionVO.setSize(10);
        List<ArticleBackDTO> articleBackDTOS = articleMapper.listArticlesBack(conditionVO);
        articleBackDTOS.forEach(item-> System.out.println(item));
    }
    @Test
    void testListArticleRecommendDTO(){
        List<ArticleRecommendDTO> articleRecommendDTOS = articleMapper.listArticleRecommends(44);
        articleRecommendDTOS.forEach(item-> System.out.println(item));
    }
    /**
     * //ready to test
    // public ArticleDTO selectArticleById(Integer articleId) {*/

    @Test
    void TestArticlePreviewDTOMap(){
        ConditionVO conditionVO = new ConditionVO();
        conditionVO.setCategoryId(13);
        conditionVO.setCurrent(0);
        conditionVO.setSize(10);
        List<ArticlePreviewDTO> articlePreviewDTOS = articleMapper.listArticlesByCondition(conditionVO);
        articlePreviewDTOS.forEach(item-> System.out.println(item));
    }

    @Test
    void TestRabbitMQ(){
        rabbitTemplate.convertAndSend(RabbitMQPreFixConst.EMAIL_EXCHANGE,"","合理！");
    }

    @Test
    void TestSendEmail(){
    }
    @Test
    void TestSelectUsers(){
        ConditionVO conditionVO = new ConditionVO();
        conditionVO.setCurrent(0);
        conditionVO.setSize(2);
        conditionVO.setKeywords("shit");
        List<UserBackDTO> userBackDTOS = userAuthMapper.selectUsers(conditionVO);
        System.out.println(userBackDTOS);
        if(userBackDTOS!=null){
            userBackDTOS.forEach(item-> System.out.println(item));
            System.out.println("---------------------------------not null=====================================");
            if(userBackDTOS.isEmpty()){
                System.out.println("=============================empty==============================");
            }
        } else
            System.out.println("---------------------------------null=====================================");
    }

    @Autowired
    private RoleMapper roleMapper;
    @Test
    void testListRoles(){
        ConditionVO conditionVO = ConditionVO.builder()
                .current(0)
                .size(10)
                .build();
        List<RoleDTO> roleDTOS = roleMapper.listRoles(conditionVO);
        System.out.println("================================");
        roleDTOS.forEach(item-> System.out.println(item));
    }


    @Test
    void testAntMatcher(){
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        boolean match = antPathMatcher.match( "/a/b","/a");
        System.out.println("是否匹配："+match);
    }

    @Autowired
    private ResourceMapper resourceMapper;
    @Test
    void testListResources(){
        List<ResourceDTO> resourceDTOS = resourceMapper.listResources();
        resourceDTOS.forEach(item-> System.out.println(item));
    }

    @Test
    void testListResourceOption(){
        List<labelOptionDTO> labelOptionDTOS = resourceMapper.listResourceOption();
        labelOptionDTOS.forEach(item-> System.out.println(item));
    }

    @Test
    void testListArticleRank(){
        ArrayList<Integer> integers = new ArrayList<>();
        integers.add(36);
        integers.add(41);
        List<Article> articles = articleMapper.listArticleRank(integers);
        articles.forEach(item-> System.out.println(item));
    }
    private CommentMapper commentMapper;
    @Test
    void testListReplies(){
        List<Integer> list = new ArrayList();
        list.add(264);
        list.add(265);
        commentMapper.listReplies(list);
    }


}
