package org.example.sqlExamples;

import org.example.EmployeeImmutable;

import org.example.sqlExamples.entity.Customer;
import org.example.sqlExamples.entity.Order;
import org.example.sqlExamples.entity.Product;
import org.example.sqlExamples.entity.Student;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

public class SqlRunnerHibernate { // using Hibernate with Postgres
  private static SessionFactory sessionFactory; // Hibernate的"重量级"对象，应用启动时创建一次， singleton

  public static void main(String[] args) throws Exception {
    // 1. Initialize Hibernate SessionFactory
    initializeHibernate();

    try (Session session = sessionFactory.openSession()) {
      // 2. execute schema.sql and data.sql
      executeSchemaAndData(session);

      // 3. execute practice-queries.sql using HQL/JPQL and native SQL
      executePracticeQueries(session);

      // 4. Hibernate query example (equivalent to PreparedStatement example)
      System.out.println("\n=== Hibernate Query example ===");
      queryStudentByAge(session, 20);

      // 5. Transaction practice using Hibernate
      System.out.println("\n=== Hibernate Transaction Practice ===");
      transactionPractice();

      // 6. Join query examples
      System.out.println("\n=== Hibernate Join Query Examples ===");
      demonstrateInnerJoin(session);
      demonstrateLeftJoin(session);

      // 7. trigger and stored procedure examples
      System.out.println("\n=== Hibernate Trigger and Stored Procedure Examples ===");
      demonstrateTrigger(session);
      demonstrateStoredProcedure(session);

    } finally {
      // Close SessionFactory
      if (sessionFactory != null) {
        sessionFactory.close();
      }
    }
  }

  private static void initializeHibernate() { // 这部分逻辑也可以单独放到文件hibernate.cfg.xml里面
    try {
      // Create configuration
      Configuration configuration = new Configuration();

      // Configure database connection
      configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
      configuration.setProperty("hibernate.connection.url", "jdbc:postgresql://localhost:5432/mydb");
      configuration.setProperty("hibernate.connection.username", "admin");
      configuration.setProperty("hibernate.connection.password", "admin123");

      // Configure Hibernate properties
      configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
      configuration.setProperty("hibernate.show_sql", "true");
      configuration.setProperty("hibernate.format_sql", "true");
      configuration.setProperty("hibernate.hbm2ddl.auto", "none"); // We'll handle schema manually

      // Add annotated classes - 告诉Hibernate这个类是一个实体类，需要进行ORM映射 - Java对象与数据库表
      // 如果不add，Hibernate不知道Student类的存在，无法进行查询
      configuration.addAnnotatedClass(Student.class);
      configuration.addAnnotatedClass(Product.class);
      configuration.addAnnotatedClass(Customer.class);
      configuration.addAnnotatedClass(Order.class);
      configuration.addAnnotatedClass(EmployeeImmutable.class);

      // Build SessionFactory
      sessionFactory = configuration.buildSessionFactory();

    } catch (Exception e) {
      System.err.println("Failed to create SessionFactory: " + e.getMessage());
      throw new RuntimeException(e);
    }
  }

  private static void executeSchemaAndData(Session session) throws Exception {
    Transaction transaction = session.beginTransaction();
    try {
      // Execute schema.sql
      String schema = Files.readString(Paths.get("src/main/resources/sql/schema.sql"));
      session.createNativeQuery(schema).executeUpdate();

      // Execute data.sql
      String data = Files.readString(Paths.get("src/main/resources/sql/data.sql"));
      session.createNativeQuery(data).executeUpdate();

      transaction.commit();
      System.out.println("Schema and data executed successfully!");
    } catch (Exception e) {
      transaction.rollback();
      throw e;
    }
  }

  private static void executePracticeQueries(Session session) throws Exception {
    // Read and execute practice queries
    String[] queries = Files.readString(Paths.get("src/main/resources/sql/practice-queries.sql")).split(";");

    for (String queryStr : queries) {
      queryStr = queryStr.trim();
      if (!queryStr.isEmpty() && !queryStr.startsWith("--")) {
        System.out.println("Running Query: " + queryStr);

        try {
          // Use native SQL query for flexibility
          Query<?> query = session.createNativeQuery(queryStr);
          List<?> results = query.getResultList();

          // Print results
          for (Object result : results) {
            if (result instanceof Object[]) {
              // Multiple columns
              Object[] row = (Object[]) result;
              for (int i = 0; i < row.length; i++) {
                System.out.print(row[i]);
                if (i < row.length - 1) System.out.print(" | ");
              }
              System.out.println();
            } else {
              // Single column
              System.out.println(result);
            }
          }
        } catch (Exception e) {
          System.out.println("Query execution failed: " + e.getMessage());
        }
        System.out.println();
      }
    }
  }

  public static void queryStudentByAge(Session session, int minAge) {
    System.out.println("search student whose age is larger than " + minAge + ":");

    // Using HQL (Hibernate Query Language)
    String hql = "FROM Student s WHERE s.age > :minAge";
    Query<Student> query = session.createQuery(hql, Student.class);
    query.setParameter("minAge", minAge);

    List<Student> students = query.getResultList();
    for (Student student : students) {
      System.out.println(student);
    }
    System.out.println();
  }

  public static void transactionPractice() {
    System.out.println("开始Hibernate Transaction练习...");

    // 2. 演示成功的事务
    System.out.println("\n--- 演示成功事务 ---");
    successfulTransaction();

    // 3. 演示回滚的事务
    System.out.println("\n--- 演示回滚事务 ---");
    rollbackTransaction();
  }

  private static void successfulTransaction() {
    Session session = sessionFactory.openSession();
    Transaction transaction = session.beginTransaction();

    try {
      // 添加一个新学生
      Student newStudent = new Student(9, "Transaction", "Test", "transaction.test@email.com",
          22, "A", LocalDate.of(2024, 3, 1), new BigDecimal("3.80"));
      session.save(newStudent);
      System.out.println("插入新学生 Transaction Test");

      // 更新现有学生Alice Johnson的GPA
      String hql = "UPDATE Student SET gpa = :newGpa WHERE firstName = :firstName AND lastName = :lastName";
      Query<?> updateQuery = session.createQuery(hql);
      updateQuery.setParameter("newGpa", new BigDecimal("3.95"));
      updateQuery.setParameter("firstName", "Alice");
      updateQuery.setParameter("lastName", "Johnson");
      int updateResult = updateQuery.executeUpdate();
      System.out.println("更新Alice Johnson的GPA到3.95，影响行数: " + updateResult);

      // 提交事务
      transaction.commit();
      System.out.println("事务提交成功！");

      // 验证：查询Alice的GPA是否真的更新了
      String verifyHql = "FROM Student WHERE firstName = 'Alice' AND lastName = 'Johnson'";
      Query<Student> verifyQuery = session.createQuery(verifyHql, Student.class);
      Student alice = verifyQuery.uniqueResult();
      if (alice != null) {
        System.out.printf("验证结果: %s %s 的GPA现在是 %.2f%n",
            alice.getFirstName(), alice.getLastName(), alice.getGpa());
      }

    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      System.out.println("事务执行失败，已回滚: " + e.getMessage());
    } finally {
      session.close();
    }
  }

  private static void rollbackTransaction() {
    Session session = sessionFactory.openSession();
    Transaction transaction = session.beginTransaction();

    try {
      // 先执行一个正常的插入
      Student rollbackStudent = new Student(10, "Rollback", "Test", "rollback.test@email.com",
          25, "B", LocalDate.of(2024, 3, 1), new BigDecimal("2.50"));
      session.save(rollbackStudent);
      System.out.println("插入测试学生 Rollback Test");

      // 然后执行一个会失败的操作 (故意制造错误)
      System.out.println("尝试执行错误的SQL语句...");
      session.createNativeQuery("UPDATE non_existent_table SET value = 1").executeUpdate();

      // 如果没有异常，提交事务
      transaction.commit();
      System.out.println("事务提交成功！");

    } catch (Exception e) {
      // 捕获异常，回滚事务
      if (transaction != null) {
        transaction.rollback();
      }
      System.out.println("捕获到异常，事务已回滚: " + e.getMessage());
      System.out.println("所有操作都被撤销，包括之前成功的插入操作");

      // 验证：查询Rollback Test学生是否真的不存在（被回滚了）
      try {
        String countHql = "SELECT COUNT(*) FROM Student WHERE firstName = 'Rollback' AND lastName = 'Test'";
        Query<Long> countQuery = session.createQuery(countHql, Long.class);
        Long count = countQuery.uniqueResult();
        System.out.printf("验证结果: 数据库中名为 'Rollback Test' 的学生数量: %d (应该是0，证明rollback成功)%n", count);
      } catch (Exception verifyException) {
        System.out.println("验证查询失败: " + verifyException.getMessage());
      }
    } finally {
      session.close();
    }
  }

  /**
   * 演示使用Hibernate进行Inner Join 查询
   * 这是一个内连接(INNER JOIN)，只会返回Order有对应Customer的记录
   * 返回结果中： 每行代表一个订单及其关联的客户信息。如果一个客户有多个订单，该客户会出现在多行中。
   */
  private static void demonstrateInnerJoin(Session session) {
    System.out.println("=== 演示Hibernate Inner Join查询 ===");

    try {
      // 使用HQL显式join语法 - 最直观的inner join示例
      String hql = "SELECT c.customerId, c.firstName, c.lastName, " +
          "o.orderId, o.orderDate, o.totalAmount " +
          "FROM Order o " +
          "JOIN o.customer c";

      Query<Object[]> query = session.createQuery(hql, Object[].class);
      List<Object[]> results = query.getResultList();

      System.out.println("客户ID | 客户名 | 订单ID | 订单日期 | 订单金额");
      System.out.println("--------------------------------------------");

      if (results.isEmpty()) {
        System.out.println("没有找到匹配的数据");
      } else {
        for (Object[] row : results) {
          System.out.printf("%d | %s %s | %d | %s | %.2f%n",
              row[0], row[1], row[2], row[3], row[4], row[5]);
        }
      }

      // 原生SQL实现相同的inner join
      System.out.println("\n使用原生SQL:");
      String nativeSql = "SELECT c.customer_id, c.first_name, c.last_name, " +
          "o.order_id, o.order_date, o.total_amount " +
          "FROM orders o " +
          "INNER JOIN customers c ON o.customer_id = c.customer_id";

      Query<Object[]> nativeQuery = session.createNativeQuery(nativeSql);
      List<Object[]> nativeResults = nativeQuery.getResultList();

      if (!nativeResults.isEmpty()) {
        System.out.println("原生SQL结果数量: " + nativeResults.size());
      }
    } catch (Exception e) {
      System.err.println("执行Inner Join查询时出错: " + e.getMessage());
    }
  }

  /**
   * 演示使用Hibernate进行Left Join查询
   * LEFT JOIN方法中的额外处理主要是针对null值的处理：
   */
  private static void demonstrateLeftJoin(Session session) {
    System.out.println("\n=== 演示Hibernate Left Join查询 ===");

    try {
      // HQL实现left join
      String hql = "SELECT c.customerId, c.firstName, c.lastName, " +
          "o.orderId, o.orderDate, o.totalAmount " +
          "FROM Customer c " +
          "LEFT JOIN Order o ON c.customerId = o.customer.customerId";

      Query<Object[]> query = session.createQuery(hql, Object[].class); // Query<Object[]> - 泛型Query对象
      List<Object[]> results = query.getResultList();

      System.out.println("客户ID | 客户名 | 订单ID | 订单日期 | 订单金额");
      System.out.println("--------------------------------------------");

      if (results.isEmpty()) {
        System.out.println("没有找到匹配的数据");
      } else {
        for (Object[] row : results) {
          // 简洁处理null值
          Integer orderId = (Integer)row[3];
          String orderIdStr = orderId != null ? orderId.toString() : "NULL";

          LocalDate orderDate = (LocalDate)row[4];
          String dateStr = orderDate != null ? orderDate.toString() : "NULL";

          BigDecimal amount = (BigDecimal)row[5];
          String amountStr = amount != null ? amount.toString() : "NULL";

          // %s %s将firstName和lastName（对应row[1]和row[2]）合并为一个显示列
          System.out.printf("%d | %s %s | %s | %s | %s%n", //
              row[0], row[1], row[2], orderIdStr, dateStr, amountStr);
        }

        // 统计没有订单的客户数量，这是LEFT JOIN特有的，因为只有LEFT JOIN才会包含没有关联订单的客户
        long customersWithoutOrders = results.stream()
            .filter(row -> row[3] == null)
            .count();

        System.out.println("\n没有订单的客户数量: " + customersWithoutOrders);
      }
    } catch (Exception e) {
      System.err.println("执行Left Join查询时出错: " + e.getMessage());
    }
  }


  // trigger and stored procedure
  /**
   * 演示触发器的基本用法
   * 这个方法展示如何通过Hibernate操作触发数据库触发器
   */
  private static void demonstrateTrigger(Session session) {
    System.out.println("\n=== 简单触发器演示 ===");
    Transaction tx = session.beginTransaction();

    try {
      // 插入新学生，这会自动触发触发器
      Student newStudent = new Student(20, "Trigger", "Test", "trigger.test@email.com",
          21, "B", LocalDate.now(), new BigDecimal("3.50"));
      session.save(newStudent);
      tx.commit();

      // 查询审计表，验证触发器已执行
      String sql = "SELECT * FROM students_audit WHERE student_id = 20";
      List<Object[]> results = session.createNativeQuery(sql).getResultList();

      if (!results.isEmpty()) {
        Object[] row = results.get(0);
        System.out.println("触发器执行确认: ");
        System.out.println("操作: " + row[1] + ", 学生ID: " + row[2] + ", 时间: " + row[3]);
      } else {
        System.out.println("触发器似乎没有执行");
      }
    } catch (Exception e) {
      tx.rollback();
      System.err.println("触发器演示失败: " + e.getMessage());
    }
  }

  /**
   * 演示存储过程和函数的基本用法
   * 这个方法展示如何通过Hibernate调用数据库存储过程和函数
   */
  private static void demonstrateStoredProcedure(Session session) {
    System.out.println("\n=== 简单存储过程演示 ===");

    try {
      // 1. 调用存储过程
      Transaction tx = session.beginTransaction();
      Query<?> procQuery = session.createNativeQuery("CALL update_student_grade(?, ?)");
      procQuery.setParameter(1, 1); // 使用位置参数
      procQuery.setParameter(2, "A");
      procQuery.executeUpdate();
      tx.commit();
      System.out.println("存储过程已执行: 已将学生ID=1的成绩更新为A");

      // 2. 调用存储函数 - 使用位置参数
      String funcSql = "SELECT * FROM get_students_by_grade(?)";
      Query<Object[]> funcQuery = session.createNativeQuery(funcSql); // createNativeQuery允许执行原生的SQL查询（而不是Hibernate的HQL查询语言）。
      funcQuery.setParameter(1, "A"); // 使用位置参数
      List<Object[]> results = funcQuery.getResultList();

      System.out.println("\n调用存储函数结果 (等级为A的学生):");
      for (Object[] row : results) {
        System.out.printf("ID: %s, 姓名: %s, 等级: %s%n", row[0], row[1], row[2]);
      }
    } catch (Exception e) {
      System.err.println("存储过程演示失败: " + e.getMessage());
      e.printStackTrace(); // 打印完整堆栈以便调试
    }
  }
}