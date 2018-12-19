package com.beidou.springsecurity.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfiguration {
    public static final String DATASOURCE = "datasource";

    @Bean(name = DATASOURCE)
    @Qualifier("dataSource")
    @ConfigurationProperties(prefix = "spring.datasource.druid")
    @Primary
    public DataSource dataSourceOne() {
        // Filled up with the properties specified about thanks to Spring Boot black magic
        return new DruidDataSource();
    }

}
