package com.myspace.wiki.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") //针对所有的请求地址
                .allowedOriginPatterns("*")//接口的允许来源
                .allowedHeaders(CorsConfiguration.ALL)
                .allowedMethods(CorsConfiguration.ALL)
                .allowCredentials(true)//允许前端的派凭证信息 cookie等
                .maxAge(3600); // 1小时内不需要再预检（发OPTIONS请求，前端前发送OPTIONS请求才判断接口是否存在,是否正常）
    }

}
