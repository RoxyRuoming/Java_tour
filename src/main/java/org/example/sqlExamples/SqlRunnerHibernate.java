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
  private static SessionFactory sessionFactory; // Hibernate的"重量级"对象，应用启动时创建一次

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

    } finally {
      // Close SessionFactory
      if (sessionFactory != null) {
        sessionFactory.close();
      }
    }
  }

  private static void initializeHibernate() {
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
}