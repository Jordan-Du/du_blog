package com.du.du_blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.spi.DocumentationType.SWAGGER_2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket docket(){


        return new Docket(SWAGGER_2)
                .apiInfo(apiInfo())
                .groupName("du")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.du.du_blog")).build();//配置显示哪些接口
    }

    public ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("du_blog的api文档")
                .description("vue+springBoot博客项目")
                .version("1.0")
                .build();
    }

    /**
     * 注入RestTemplate，用于操作restful接口信息
     * @return
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
