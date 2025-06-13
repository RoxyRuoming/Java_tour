package com.example.springDemo.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    entityManagerFactoryRef = "mysqlEntityManagerFactory",
    transactionManagerRef = "mysqlTransactionManager",
    basePackages = {"com.example.springDemo.repository.mysql"}
)
public class MysqlDbConfig {

  @Bean(name = "mysqlDataSource")
  @ConfigurationProperties(prefix = "spring.datasource.mysql")
  public DataSource dataSource() {
    return DataSourceBuilder.create().build();
  }

  @Bean(name = "mysqlEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
      EntityManagerFactoryBuilder builder,
      @Qualifier("mysqlDataSource") DataSource dataSource) {
    Map<String, Object> properties = new HashMap<>();
    properties.put("hibernate.hbm2ddl.auto", "update");
//    properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");

    return builder
        .dataSource(dataSource)
        .packages("com.example.springDemo.model")
        .persistenceUnit("mysql")
        .properties(properties)
        .build();
  }

  @Bean(name = "mysqlTransactionManager")
  public PlatformTransactionManager transactionManager(
      @Qualifier("mysqlEntityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory) {
    return new JpaTransactionManager(entityManagerFactory.getObject());
  }
}