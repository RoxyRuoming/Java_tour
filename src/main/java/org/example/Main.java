package org.example;

import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
  public static void main(String[] args) {
    try {
      System.out.println("Reading SQL file...");
      String sql = new String(Files.readAllBytes(
          Paths.get("src/main/resources/sql/init.sql")));

      System.out.println("Executing SQL: " + sql);

      try (Session session = HibernateUtil.getSessionFactory().openSession()) {
        Transaction transaction = session.beginTransaction();

        session.createNativeMutationQuery(sql).executeUpdate();

        transaction.commit();
        System.out.println("Table created successfully!");
      }
    } catch (IOException e) {
      System.err.println("Error reading SQL file:");
      e.printStackTrace();
    } catch (Exception e) {
      System.err.println("Error executing SQL:");
      e.printStackTrace();
    } finally {
      HibernateUtil.shutdown();
    }
  }
}