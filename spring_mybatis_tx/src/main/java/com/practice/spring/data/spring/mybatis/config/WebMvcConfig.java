package com.practice.spring.data.spring.mybatis.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@ComponentScan("com.practice.spring.data.spring_mybatis_tx.web")
public class WebMvcConfig implements WebMvcConfigurer {
}

