package org.example.sqlExamples.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "customers")
@Data  // 自动生成getter/setter/toString/equals/hashCode
@NoArgsConstructor  // 生成无参构造函数 (Hibernate需要)
@AllArgsConstructor // 生成全参构造函数
public class Customer {
  @Id
  @Column(name = "customer_id")
  private Integer customerId;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "email")
  private String email;

  @Column(name = "phone")
  private String phone;

  @Column(name = "city")
  private String city;

  @Column(name = "country")
  private String country;

  @Column(name = "registration_date")
  private LocalDate registrationDate;

  // 所有getter/setter/toString方法都由Lombok自动生成！
}