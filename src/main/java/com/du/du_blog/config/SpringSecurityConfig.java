package com.du.du_blog.config;

import com.du.du_blog.filter.JWTFilter;
import com.du.du_blog.handler.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;
    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;
    @Autowired
    private LogoutSuccessHandler logoutSuccessHandler;
    @Autowired
    private FilterInvocationSecurityMetadataSourceImpl filterInvocationSecurityMetadataSource;
    @Autowired
    private AccessDecisionManagerImpl accessDecisionManager;
    @Autowired
    private AccessDeniedHandlerImpl accessDeniedHandler;
    @Autowired
    private AuthenticationEntryPointImpl authenticationEntryPoint;


    //??????????????????
    @Bean
    public CorsFilter corsFilter() {
        //1. ?????? CORS????????????
        CorsConfiguration config = new CorsConfiguration();
        //?????????????????????
        config.addAllowedOriginPattern("*");
        //???????????? Cookie
        config.setAllowCredentials(true);
        //????????????????????????
        config.addAllowedMethod("*");
        //????????????????????????????????????
        config.addAllowedHeader("*");
        //????????????????????????
        config.addExposedHeader("*");
        //2. ??????????????????
        UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
        corsConfigurationSource.registerCorsConfiguration("/**", config);
        //3. ????????????CorsFilter
        return new CorsFilter(corsConfigurationSource);
    }

    @Bean
    public JWTFilter jwtFilter() throws Exception {
        return new JWTFilter(authenticationManager());
    }


    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        //BCrypt?????????????????????????????????????????????2^strength
        return new BCryptPasswordEncoder(10);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //????????????
        http
                //????????????????????????
                .cors()
                .and()
                .formLogin().loginProcessingUrl("/login")
                //????????????
                .successHandler(authenticationSuccessHandler)
                //????????????
                .failureHandler(authenticationFailureHandler)
                .and()
                //????????????
                .logout()
                .logoutSuccessUrl("/logout")
                .logoutSuccessHandler(logoutSuccessHandler)
                .and()
                //??????jwt?????????
                .addFilter(jwtFilter())
                //????????????
                .authorizeRequests()
                .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                    @Override
                    public <O extends FilterSecurityInterceptor> O postProcess(O o) {
                        o.setSecurityMetadataSource(filterInvocationSecurityMetadataSource);
                        o.setAccessDecisionManager(accessDecisionManager);
                        return o;
                    }
                })
                .anyRequest()
                .permitAll()
                .and()
                //????????????????????????
                .csrf().disable()
                .exceptionHandling()
                //???????????????
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                ;
    }
}
