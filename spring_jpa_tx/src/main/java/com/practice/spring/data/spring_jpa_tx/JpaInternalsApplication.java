package com.practice.spring.data.spring_jpa_tx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.practice.spring.data.spring_jpa_tx.entity")
@EnableJpaRepositories("com.practice.spring.data.spring_jpa_tx.repository")
public class JpaInternalsApplication {

    public static void main(String[] args) {
        SpringApplication.run(JpaInternalsApplication.class, args);
    }
}

