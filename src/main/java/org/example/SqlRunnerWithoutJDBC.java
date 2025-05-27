package org.example;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SqlRunnerWithoutJDBC { // ProcessBuilder

  private static final String CONTAINER_NAME = "postgres_db";
  private static final String DB_USER = "admin";
  private static final String DB_NAME = "mydb";

  public static void main(String[] args) throws Exception {
    System.out.println("=== SQL Runner (无JDBC版本) ===");

    // 1. 测试连接
    if (!testConnection()) {
      System.err.println("❌ 无法连接到PostgreSQL容器");
      return;
    }
    System.out.println("✅ 数据库连接测试成功");

    // 2. execute schema.sql
    System.out.println("\n=== 执行 schema.sql ===");
    String schema = Files.readString(Paths.get("src/main/resources/sql/schema.sql"));
    if (executeSql(schema)) {
      System.out.println("✅ 表结构创建完成");
    }

    // 3. execute data.sql
    System.out.println("\n=== 执行 data.sql ===");
    String data = Files.readString(Paths.get("src/main/resources/sql/data.sql"));
    if (executeSql(data)) {
      System.out.println("✅ 测试数据插入完成");
    }

    // 4. execute practice-queries.sql
    System.out.println("\n=== 执行 practice-queries.sql ===");
    String queryContent = Files.readString(Paths.get("src/main/resources/sql/practice-queries.sql"));
    String[] queries = queryContent.split(";");

    for (String query : queries) {
      query = query.trim();
      if (!query.isEmpty() && !query.startsWith("--")) {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("Running Query: " + query);
        System.out.println("Result:");
        executeQuery(query);
      }
    }

    System.out.println("\n🎉 所有SQL执行完成！");
  }

  /**
   * 测试数据库连接
   */
  private static boolean testConnection() {
    return executeQuery("SELECT version();");
  }

  /**
   * 执行SQL语句（INSERT, UPDATE, DELETE, CREATE等）
   */
  private static boolean executeSql(String sql) {
    try {
      List<String> command = new ArrayList<>();
      command.add("docker");
      command.add("exec");
      command.add("-i");
      command.add(CONTAINER_NAME);
      command.add("psql");
      command.add("-U");
      command.add(DB_USER);
      command.add("-d");
      command.add(DB_NAME);
      command.add("-f");
      command.add("/dev/stdin");

      ProcessBuilder pb = new ProcessBuilder(command);
      pb.redirectErrorStream(true);
      Process process = pb.start();

      // 将SQL发送到进程
      try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
        writer.write(sql);
        writer.newLine();
      }

      // 读取执行结果
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
        String line;
        while ((line = reader.readLine()) != null) {
          System.out.println(line);
        }
      }

      int exitCode = process.waitFor();
      return exitCode == 0;

    } catch (IOException | InterruptedException e) {
      System.err.println("❌ 执行SQL失败: " + e.getMessage());
      return false;
    }
  }

  /**
   * 执行查询语句并显示结果
   */
  private static boolean executeQuery(String query) {
    try {
      List<String> command = new ArrayList<>();
      command.add("docker");
      command.add("exec");
      command.add(CONTAINER_NAME);
      command.add("psql");
      command.add("-U");
      command.add(DB_USER);
      command.add("-d");
      command.add(DB_NAME);
      command.add("-c");
      command.add(query);

      ProcessBuilder pb = new ProcessBuilder(command);
      pb.redirectErrorStream(true);
      Process process = pb.start();

      // 读取查询结果
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
        String line;
        while ((line = reader.readLine()) != null) {
          System.out.println(line);
        }
      }

      int exitCode = process.waitFor();
      return exitCode == 0;

    } catch (IOException | InterruptedException e) {
      System.err.println("❌ 执行查询失败: " + e.getMessage());
      return false;
    }
  }
}