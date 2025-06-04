package org.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "students") // Hibernate 会自动将类字段与数据库表字段关联
public class Student {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private String name;
  private Integer age;

  // Getters and setters
  public int getId() { return id; }
  public void setId(int id) { this.id = id; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public Integer getAge() { return age; }
  public void setAge(Integer age) { this.age = age; }
}