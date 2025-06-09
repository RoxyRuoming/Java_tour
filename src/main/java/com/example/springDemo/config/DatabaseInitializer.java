package com.example.springDemo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Component
public class DatabaseInitializer implements CommandLineRunner {

  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public DatabaseInitializer(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void run(String... args) throws Exception {
    // Read SQL file
    ClassPathResource resource = new ClassPathResource("sql/init.sql");
    String sql = new BufferedReader(
        new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))
        .lines()
        .collect(Collectors.joining("\n"));

    // Execute SQL statements
    jdbcTemplate.execute(sql);

    System.out.println("Database initialized with SQL script");
  }
}