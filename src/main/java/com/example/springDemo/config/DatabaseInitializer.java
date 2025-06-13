package com.example.springDemo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DatabaseInitializer implements CommandLineRunner {

  private final DataSource postgresDataSource;
  private final DataSource mysqlDataSource;

  @Autowired
  public DatabaseInitializer(
      @Qualifier("postgresDataSource") DataSource postgresDataSource,
      @Qualifier("mysqlDataSource") DataSource mysqlDataSource) {
    this.postgresDataSource = postgresDataSource;
    this.mysqlDataSource = mysqlDataSource;
  }

  @Override
  public void run(String... args) throws Exception {
    // 初始化 PostgreSQL
    JdbcTemplate postgresJdbcTemplate = new JdbcTemplate(postgresDataSource);
    ClassPathResource postgresResource = new ClassPathResource("sql/postgres-init.sql");
    String postgresSql = new BufferedReader(
        new InputStreamReader(postgresResource.getInputStream(), StandardCharsets.UTF_8))
        .lines()
        .collect(Collectors.joining("\n"));

    // 分别执行每条SQL语句
    executeStatements(postgresJdbcTemplate, postgresSql);
    System.out.println("PostgreSQL database initialized with SQL script");

    // 初始化 MySQL
    JdbcTemplate mysqlJdbcTemplate = new JdbcTemplate(mysqlDataSource);
    ClassPathResource mysqlResource = new ClassPathResource("sql/mysql-init.sql");
    String mysqlSql = new BufferedReader(
        new InputStreamReader(mysqlResource.getInputStream(), StandardCharsets.UTF_8))
        .lines()
        .collect(Collectors.joining("\n"));

    // 分别执行每条SQL语句
    executeStatements(mysqlJdbcTemplate, mysqlSql);
    System.out.println("MySQL database initialized with SQL script");
  }

  private void executeStatements(JdbcTemplate jdbcTemplate, String sql) {
    // 按分号分割SQL语句，并过滤空语句
    List<String> statements = Arrays.stream(sql.split(";"))
        .map(String::trim)
        .filter(stmt -> !stmt.isEmpty())
        .collect(Collectors.toList());

    // 逐个执行每条语句
    for (String statement : statements) {
      jdbcTemplate.execute(statement);
    }
  }
}