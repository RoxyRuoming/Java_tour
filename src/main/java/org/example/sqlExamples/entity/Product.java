package org.example.sqlExamples.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "products")
@Data  // 自动生成getter/setter/toString/equals/hashCode
@NoArgsConstructor  // 生成无参构造函数 (Hibernate需要)
@AllArgsConstructor // 生成全参构造函数
public class Product {
  @Id
  @Column(name = "product_id")
  private Integer productId;

  @Column(name = "product_name")
  private String productName;

  @Column(name = "category")
  private String category;

  @Column(name = "price")
  private BigDecimal price;

  @Column(name = "stock_quantity")
  private Integer stockQuantity;

  @Column(name = "created_date")
  private LocalDate createdDate;

  // 所有getter/setter/toString方法都由Lombok自动生成！
}