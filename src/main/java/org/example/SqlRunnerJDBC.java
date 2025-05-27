package org.example;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class SqlRunnerJDBC {
  public static void main(String[] args) throws Exception {
    // 1. connect h2 database
//    Connection conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");

    // postgres connection
    Connection conn = DriverManager.getConnection(
        "jdbc:postgresql://localhost:5432/postgres",
        "admin",
        "admin123"  // 替换为你的实际密码
    );

    Statement stmt = conn.createStatement();

    // 2. execute schema.sql
    String schema = Files.readString(Paths.get("src/main/resources/sql/schema.sql"));
    stmt.execute(schema);

    // 3. execute data.sql
    String data = Files.readString(Paths.get("src/main/resources/sql/data.sql"));
    stmt.execute(data);

    // 4. execute practice-queries.sql (recommend execute step by step)
    String[] queries = Files.readString(Paths.get("src/main/resources/sql/practice-queries.sql")).split(";");
    for (String query : queries) {
      query = query.trim();
      if (!query.isEmpty()) {
        System.out.println("Running Query: " + query);
        var rs = stmt.executeQuery(query);
        var meta = rs.getMetaData();
        int colCount = meta.getColumnCount();
        while (rs.next()) {
          for (int i = 1; i <= colCount; i++) {
            System.out.print(rs.getString(i));
            if (i < colCount) System.out.print(" | ");
          }
          System.out.println();
        }
      }
    }

    stmt.close();
    conn.close();
  }
}
