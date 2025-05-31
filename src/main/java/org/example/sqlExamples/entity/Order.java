package org.example.sqlExamples.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "orders")
@Data  // 自动生成getter/setter/toString/equals/hashCode
@NoArgsConstructor  // 生成无参构造函数 (Hibernate需要)
@AllArgsConstructor // 生成全参构造函数
@ToString(exclude = {"customer", "product"}) // 排除懒加载字段，避免循环引用和N+1查询
public class Order {
  @Id
  @Column(name = "order_id")
  private Integer orderId;

  // 定义了Order和Customer之间的多对一关系。Customer实体本身没有反向引用（即没有定义一对多关系到Order）
  // fetch = FetchType.LAZY 指定加载策略为懒加载，是hibernate lazy loading 的一种实现方式
  // 意味着当加载Order对象时，不会立即加载关联的Customer对象，只有在实际访问customer属性时才会加载
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "customer_id")
  private Customer customer;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id")
  private Product product;

  @Column(name = "quantity")
  private Integer quantity;

  @Column(name = "order_date")
  private LocalDate orderDate;

  @Column(name = "total_amount")
  private BigDecimal totalAmount;

  @Column(name = "status")
  private String status;

  // 如果需要自定义toString显示关联对象信息，可以重写：
  public String getDisplayInfo() {
    return String.format("Order{id=%d, customer=%s, product=%s, quantity=%d, amount=%.2f, status='%s'}",
        orderId,
        customer != null ? customer.getFirstName() + " " + customer.getLastName() : "Unknown",
        product != null ? product.getProductName() : "Unknown",
        quantity, totalAmount, status);
  }
}