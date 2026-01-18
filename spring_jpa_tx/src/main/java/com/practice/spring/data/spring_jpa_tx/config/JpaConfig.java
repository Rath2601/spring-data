//package com.practice.spring.data.spring_jpa_tx.config;
//
//import jakarta.persistence.EntityManagerFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//import javax.sql.DataSource;
//import java.util.Properties;
//
//@Configuration
//@EnableTransactionManagement
//public class JpaConfig {
//
//    @Bean
//    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
//            DataSource dataSource) {
//
//        LocalContainerEntityManagerFactoryBean emf =
//                new LocalContainerEntityManagerFactoryBean();
//
//        emf.setDataSource(dataSource);
//        emf.setPackagesToScan("com.practice.spring.data.spring_jpa_tx.entity");
//
//        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
//        adapter.setGenerateDdl(true);
//        adapter.setShowSql(true);
//
//        Properties props = new Properties();
//        props.put("hibernate.hbm2ddl.auto", "create");
//        props.put("hibernate.format_sql", "true");
//
//        emf.setJpaVendorAdapter(adapter);
//        emf.setJpaProperties(props);
//        return emf;
//    }
//
//    @Bean
//    public PlatformTransactionManager transactionManager(
//            EntityManagerFactory emf) {
//        return new JpaTransactionManager(emf);
//    }
//}
//
