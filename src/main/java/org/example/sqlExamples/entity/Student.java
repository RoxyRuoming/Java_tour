package org.example.sqlExamples.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "students")
@Data  // 自动生成getter/setter/toString/equals/hashCode
@NoArgsConstructor  // 生成无参构造函数 (Hibernate需要)
@AllArgsConstructor // 生成全参构造函数
public class Student {
  @Id
  @Column(name = "student_id")
  private Integer studentId;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "email")
  private String email;

  @Column(name = "age")
  private Integer age;

  @Column(name = "grade")
  private String grade;

  @Column(name = "enrollment_date")
  private LocalDate enrollmentDate;

  @Column(name = "gpa")
  private BigDecimal gpa;

  // 所有getter/setter/toString方法都由Lombok自动生成！
  // 如果需要自定义toString，可以override：
  @Override
  public String toString() {
    return String.format("%s %s (年龄: %d, GPA: %.2f)",
        firstName, lastName, age, gpa);
  }
}