package org.example.sqlExamples;

import java.nio.file.Files;
import java.nio.file.Paths;

// the following are JDBC package - java.sql.*
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SqlRunnerJDBC { // using Postgres
  public static void main(String[] args) throws Exception {
    // 1. connect h2 database
//    Connection conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");

    // postgres connection - using Docker container
    Connection conn = DriverManager.getConnection(
        // jdbc url format: "jdbc: ...."
        "jdbc:postgresql://localhost:5432/mydb", // 这里除了mydb还有一个postgres的默认数据库, 不要搞错
        "admin",
        "admin123"  // replaced with your real password
    );


    // recommend: PreparedStatement  ----> avoid security issue by SQL injection
    // placeholder -> 1 or 1=1
    // sql injection: SELECT * FROM LOGIN_TABLE WHERE id = 1 or 1=1;
    Statement stmt = conn.createStatement();

    // 2. execute schema.sql
    String schema = Files.readString(Paths.get("src/main/resources/sql/schema.sql")); // 使用Java 11+的Files.readString()方法读取SQL文件内容
    stmt.execute(schema);

    // 3. execute data.sql
    String data = Files.readString(Paths.get("src/main/resources/sql/data.sql"));
    stmt.execute(data);

    // 4. execute practice-queries.sql (recommend execute step by step)
    // 手动mapping到object - without the help of hibernate
    String[] queries = Files.readString(Paths.get("src/main/resources/sql/practice-queries.sql")).split(";"); // split the queries by ;
    for (String query : queries) {
      query = query.trim();
      if (!query.isEmpty()) {
        System.out.println("Running Query: " + query);
        var rs = stmt.executeQuery(query);
        var meta = rs.getMetaData();
        int colCount = meta.getColumnCount(); // dynamically get the number of cols - flexible
        while (rs.next()) {
          for (int i = 1; i <= colCount; i++) { // iterate each column
            System.out.print(rs.getString(i));
            if (i < colCount) System.out.print(" | "); // formatting
          }
          System.out.println();
        }
      }
    }

    // PrepareStatement example
    System.out.println("\n=== PreparedStatement example ===");
    queryStudentByAge(conn, 20);

    // 新增：Transaction 练习
    System.out.println("\n=== Transaction Practice ===");
    transactionPractice(conn);

    stmt.close();
    conn.close();
  }

  // main（as a static method）直接调用的方法是static
  // without static -- should be someObject.queryStudentByAge(...)
  public static void queryStudentByAge(Connection conn, int minAge) throws SQLException {
    String sql = "SELECT first_name, last_name, age, gpa FROM students WHERE age > ?";
    try (PreparedStatement pstmt = conn.prepareStatement(sql)) { // try-with-resources - automatically close resources
      pstmt.setInt(1, minAge);
      System.out.println("search student whose age is large than " + minAge + ":");
      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        System.out.printf("%s %s (年龄: %d, GPA: %.2f)%n",
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getInt("age"),
            rs.getDouble("gpa"));
      }
      System.out.println();
    }
  }

  // 新增：Transaction 操作练习方法
  public static void transactionPractice(Connection conn) throws SQLException {
    // 保存原始的自动提交状态
    boolean originalAutoCommit = conn.getAutoCommit();

    try {
      System.out.println("开始Transaction练习...");

      // 2. 演示成功的事务（独立事务）
      System.out.println("\n--- 演示成功事务 ---");
      conn.setAutoCommit(false);  // 开启事务1 // 每次调用 conn.setAutoCommit(false); 都会开始一个新的事务
      successfulTransaction(conn);
      // successfulTransaction内部会commit，事务1结束

      // 3. 演示回滚的事务（独立事务）
      System.out.println("\n--- 演示回滚事务 ---");
      conn.setAutoCommit(false);  // 开启事务2
      rollbackTransaction(conn);
      // rollbackTransaction内部会rollback，事务2结束

    } finally {
      // 恢复原始的自动提交状态
      conn.setAutoCommit(originalAutoCommit);
    }
  }

  // 成功的事务示例
  private static void successfulTransaction(Connection conn) throws SQLException {
    try {
      // 我们要添加一个新学生并更新现有学生Alice Johnson的GPA
      String insertSql = "INSERT INTO students (student_id, first_name, last_name, email, age, grade, enrollment_date, gpa) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
      String updateSql = "UPDATE students SET gpa = ? WHERE first_name = ? AND last_name = ?";

      try (PreparedStatement insertStmt = conn.prepareStatement(insertSql);
          PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

        // 插入新学生 (student_id = 9, 因为现有数据到8)
        insertStmt.setInt(1, 9);
        insertStmt.setString(2, "Transaction");
        insertStmt.setString(3, "Test");
        insertStmt.setString(4, "transaction.test@email.com");
        insertStmt.setInt(5, 22);
        insertStmt.setString(6, "A");
        insertStmt.setDate(7, java.sql.Date.valueOf("2024-03-01"));
        insertStmt.setDouble(8, 3.8);
        int insertResult = insertStmt.executeUpdate();
        System.out.println("插入新学生 Transaction Test，影响行数: " + insertResult);

        // 更新现有学生Alice Johnson的GPA (从3.85提升到3.95)
        updateStmt.setDouble(1, 3.95);
        updateStmt.setString(2, "Alice");
        updateStmt.setString(3, "Johnson");
        int updateResult = updateStmt.executeUpdate();
        System.out.println("更新Alice Johnson的GPA到3.95，影响行数: " + updateResult);

        // 提交事务
        conn.commit();
        System.out.println("事务提交成功！");

        // 验证：查询Alice的GPA是否真的更新了
        String verifySql = "SELECT first_name, last_name, gpa FROM students WHERE first_name = 'Alice' AND last_name = 'Johnson'";
        try (PreparedStatement verifyStmt = conn.prepareStatement(verifySql)) {
          ResultSet rs = verifyStmt.executeQuery();
          if (rs.next()) {
            System.out.printf("验证结果: %s %s 的GPA现在是 %.2f%n",
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getDouble("gpa"));
          }
        }
      }
    } catch (SQLException e) {
      conn.rollback();
      System.out.println("事务执行失败，已回滚: " + e.getMessage());
      throw e;
    }
  }

  // 回滚事务示例
  private static void rollbackTransaction(Connection conn) throws SQLException {
    try {
      // story: 我们想要添加一个新学生，同时更新一个产品的库存，但是会故意制造失败
      String insertSql = "INSERT INTO students (student_id, first_name, last_name, email, age, grade, enrollment_date, gpa) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
      String badUpdateSql = "UPDATE non_existent_table SET value = 1"; // 故意使用不存在的表

      try (PreparedStatement insertStmt = conn.prepareStatement(insertSql);
          Statement badStmt = conn.createStatement()) {

        // 先执行一个正常的插入 (student_id = 10)
        insertStmt.setInt(1, 10);
        insertStmt.setString(2, "Rollback");
        insertStmt.setString(3, "Test");
        insertStmt.setString(4, "rollback.test@email.com");
        insertStmt.setInt(5, 25);
        insertStmt.setString(6, "B");
        insertStmt.setDate(7, java.sql.Date.valueOf("2024-03-01"));
        insertStmt.setDouble(8, 2.5);
        int insertResult = insertStmt.executeUpdate();
        System.out.println("插入测试学生 Rollback Test，影响行数: " + insertResult);

        // 然后执行一个会失败的操作
        System.out.println("尝试执行错误的SQL语句...");
        badStmt.executeUpdate(badUpdateSql); // 这里会抛出异常

        // 如果没有异常，提交事务
        conn.commit();
        System.out.println("事务提交成功！");

      } catch (SQLException e) {
        // 捕获异常，回滚事务
        conn.rollback();
        System.out.println("捕获到异常，事务已回滚: " + e.getMessage());
        System.out.println("所有操作都被撤销，包括之前成功的插入操作");

        // 验证：查询Rollback Test学生是否真的不存在（被回滚了）
        String verifySql = "SELECT COUNT(*) as count FROM students WHERE first_name = 'Rollback' AND last_name = 'Test'";
        try (PreparedStatement verifyStmt = conn.prepareStatement(verifySql)) {
          ResultSet rs = verifyStmt.executeQuery();
          if (rs.next()) {
            int count = rs.getInt("count");
            System.out.printf("验证结果: 数据库中名为 'Rollback Test' 的学生数量: %d (应该是0，证明rollback成功)%n", count);
          }
        } catch (SQLException verifyException) {
          System.out.println("验证查询失败: " + verifyException.getMessage());
        }
      }
    } catch (SQLException e) {
      System.out.println("回滚演示过程中发生错误: " + e.getMessage());
    }
  }
}