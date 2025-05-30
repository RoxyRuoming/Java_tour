package org.example.nonsqlExamples.mongodb.entity;

import dev.morphia.annotations.*;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Entity("blog_posts")
public class MongoBlogPost {

  @Id
  private ObjectId id;

  @Property("title")
  private String title;

  @Property("content")
  private String content;

  @Property("author")
  private Author author;

  @Property("tags")
  private List<String> tags;

  @Property("status")
  private String status;

  @Property("created_at")
  private LocalDateTime createdAt;

  @Property("updated_at")
  private LocalDateTime updatedAt;

  // 无参构造函数（Morphia 必需）
  public MongoBlogPost() {}

  // 构造函数
  public MongoBlogPost(String title, String content, Author author, List<String> tags, String status) {
    this.title = title;
    this.content = content;
    this.author = author;
    this.tags = tags;
    this.status = status;
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  // 嵌套的 Author 类 - 关键修复：添加 @Embedded 注解
  @Embedded
  public static class Author {
    @Property("name")
    private String name;

    @Property("email")
    private String email;

    @Property("bio")
    private String bio;

    // 无参构造函数（Morphia 必需）
    public Author() {}

    public Author(String name, String email, String bio) {
      this.name = name;
      this.email = email;
      this.bio = bio;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    @Override
    public String toString() {
      return String.format("Author{name='%s', email='%s'}", name, email);
    }
  }

  // 静态工厂方法创建示例数据
  public static MongoBlogPost createSampleBlogPost() {
    Author author = new Author(
        "张三",
        "zhangsan@example.com",
        "资深Java开发工程师，专注于后端技术"
    );

    return new MongoBlogPost(
        "MongoDB 与 Java 集成指南",
        "本文介绍如何在 Java 项目中使用 MongoDB，包括基本的 CRUD 操作和高级查询...",
        author,
        Arrays.asList("Java", "MongoDB", "数据库", "技术"),
        "published"
    );
  }

  // Getters and Setters
  public ObjectId getId() { return id; }
  public void setId(ObjectId id) { this.id = id; }

  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }

  public String getContent() { return content; }
  public void setContent(String content) { this.content = content; }

  public Author getAuthor() { return author; }
  public void setAuthor(Author author) { this.author = author; }

  public List<String> getTags() { return tags; }
  public void setTags(List<String> tags) { this.tags = tags; }

  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }

  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

  @Override
  public String toString() {
    return String.format("BlogPost{id=%s, title='%s', author=%s, status='%s'}",
        id, title, author, status);
  }
}