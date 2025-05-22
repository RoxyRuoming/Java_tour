package org.example;

import java.util.List;
import java.util.ArrayList;

public class EmployeeSingleton {

  // 1. make the intance static
  private static volatile EmployeeSingleton INSTANCE; // volatile - thread related, jav 5+
  /*
  What happens when a variable is marked as volatile?
      • Volatile can only be applied to instance variables.
      • A volatile variable is one whose value is always written to and read from "main memory". Each
  thread has its own cache in Java. The volatile variable will not be stored on a Thread cache.
*/
  // 2. private constructor
  private EmployeeSingleton(String firstName, String lastName, String email,
      Integer password, Boolean flagged, List<Integer> list) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.password = password;
    this.flagged = flagged;
    this.list = new ArrayList<>(list); // deep copy
  }

  // 3. thread safe - avoid creating multiple instances in multi thread env
  public static EmployeeSingleton getInstance() {
    if (INSTANCE == null) { // first check（no lock） - avoid add lock for each time
      synchronized (EmployeeSingleton.class) { // add lock - synchronized 块：锁定 Employee.class，确保同一时间只有一个线程能执行初始化。
        if (INSTANCE == null) { // second check（with lock） (avoid multiple creation)
          INSTANCE = new EmployeeSingleton( // safe initialization
              "DefaultFirstName",
              "DefaultLastName",
              "default@example.com",
              12345,
              false,
              new ArrayList<>()
          );
        }
      }
    }
    return INSTANCE;
  }

//  synchronized (EmployeeSingleton.class) 表示对 EmployeeSingleton.class 这个类对象加锁。
//  only one thread can access this code block at the same time

  private String firstName;
  private String lastName;
  private String email;
  private Integer password;
  private Boolean flagged;
  private List<Integer> list;

  public String getFirstName() { return firstName; }
  public void setFirstName(String firstName) { this.firstName = firstName; }

  public String getLastName() { return lastName; }
  public void setLastName(String lastName) { this.lastName = lastName; }

  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }

  public Integer getPassword() { return password; }
  public void setPassword(Integer password) { this.password = password; }

  public Boolean getFlagged() { return flagged; }
  public void setFlagged(Boolean flagged) { this.flagged = flagged; }

  public List<Integer> getList() { return list; }
  public void setList(List<Integer> list) { this.list = list; }
}