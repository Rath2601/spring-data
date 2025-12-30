package com.practice.spring.data.spring.mybatis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.practice.spring.data.spring.mybatis.repository.mapper")
public class SpringMybatisTxApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringMybatisTxApplication.class, args);
	}

}
