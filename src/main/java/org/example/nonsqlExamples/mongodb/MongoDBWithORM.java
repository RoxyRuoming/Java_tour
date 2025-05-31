package org.example.nonsqlExamples.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.query.filters.Filters;
import org.example.nonsqlExamples.mongodb.entity.MongoBlogPost;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class MongoDBWithORM {

  private static final String CONNECTION_STRING = "mongodb://admin:admin123@localhost:27017/testdb?authSource=admin";
  private static final String DATABASE_NAME = "testdb";
  private static Datastore datastore;
  private static final Scanner scanner = new Scanner(System.in);

  public static void main(String[] args) {
    System.out.println("=== MongoDB 简化演示（仅博客文章）===");

    if (!initializeMorphia()) {
      System.err.println("❌ 连接失败");
      return;
    }

    demonstrateBlogOperations();
    scanner.close();
  }

  private static boolean initializeMorphia() {
    try {
      // 1. create MongoClient
      MongoClient mongoClient = MongoClients.create(CONNECTION_STRING);

      // 2. initialize Morphia  and create objects
      datastore = Morphia.createDatastore(mongoClient, DATABASE_NAME);

      // 映射实体类（包括嵌套类）
      datastore.getMapper().map(MongoBlogPost.class);
      // 如果有嵌套类问题，可以显式映射
      datastore.getMapper().map(MongoBlogPost.Author.class);

      // 3. 确保索引
      datastore.ensureIndexes();

      System.out.println("✅ MongoDB 连接成功");
      return true;
    } catch (Exception e) {
      System.err.println("❌ 初始化失败: " + e.getMessage());
      e.printStackTrace();
      return false;
    }
  }

  private static void demonstrateBlogOperations() {
    try {
      // 1. 创建示例博客
      MongoBlogPost post = MongoBlogPost.createSampleBlogPost();
      datastore.save(post);
      System.out.println("✅ 博客保存成功，ID: " + post.getId());

      // 2. 查询所有博客
      System.out.println("\n📝 所有博客文章:");
      List<MongoBlogPost> posts = datastore.find(MongoBlogPost.class).iterator().toList();
      posts.forEach(p -> System.out.println("- " + p.getTitle() + " (" + p.getStatus() + ")")); // lambda

      // 3. 详细查看博客内容
      System.out.println("\n📖 博客详细信息:");
      posts.forEach(p -> {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("标题: " + p.getTitle());
        System.out.println("作者: " + p.getAuthor().getName() + " (" + p.getAuthor().getEmail() + ")");
        System.out.println("简介: " + p.getAuthor().getBio());
        System.out.println("内容: " + p.getContent());
        System.out.println("标签: " + String.join(", ", p.getTags()));
        System.out.println("状态: " + p.getStatus());
        System.out.println("创建时间: " + p.getCreatedAt());
      });

      // 4. 条件查询（含Java标签的文章）
      System.out.println("\n🔍 Java相关文章:");
      List<MongoBlogPost> javaPosts = datastore.find(MongoBlogPost.class) // what happens in mongoDB: db.collection.find({ tags: { $in: ["Java"] } })
          .filter(Filters.in("tags", Arrays.asList("Java")))
          .iterator().toList();
      javaPosts.forEach(p -> System.out.println("- " + p.getTitle()));

      // 5. 按作者查询
      System.out.println("\n👤 按作者查询:");
      List<MongoBlogPost> authorPosts = datastore.find(MongoBlogPost.class)
          .filter(Filters.eq("author.name", "张三"))
          .iterator().toList();
      authorPosts.forEach(p -> System.out.println("- " + p.getTitle() + " by " + p.getAuthor().getName()));

      // 6. 统计信息
      System.out.println("\n📊 统计信息:");
      long totalPosts = datastore.find(MongoBlogPost.class).count();
      long publishedPosts = datastore.find(MongoBlogPost.class)
          .filter(Filters.eq("status", "published")).count();
      System.out.println("总文章数: " + totalPosts);
      System.out.println("已发布文章数: " + publishedPosts);

      // 7. 交互式菜单
      showInteractiveMenu();

    } catch (Exception e) {
      System.err.println("❌ 操作失败: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private static void showInteractiveMenu() {
    while (true) {
      System.out.println("\n" + "=".repeat(50));
      System.out.println("📋 请选择操作:");
      System.out.println("1. 查看所有文章");
      System.out.println("2. 添加新文章");
      System.out.println("3. 按标签搜索");
      System.out.println("4. 按作者搜索");
      System.out.println("5. 更新文章状态");
      System.out.println("6. 删除所有测试数据");
      System.out.println("0. 退出程序");
      System.out.print("请输入选项 (0-6): ");

      String choice = scanner.nextLine().trim();

      switch (choice) {
        case "1" -> viewAllPosts(); // 使用Java 14+的箭头语法(->)简化switch语句
        case "2" -> addNewPost();
        case "3" -> searchByTag();
        case "4" -> searchByAuthor();
        case "5" -> updatePostStatus();
        case "6" -> {
          if (confirmDelete()) {
            datastore.find(MongoBlogPost.class).delete();
            System.out.println("✅ 所有数据已清理");
          }
        }
        case "0" -> {
          System.out.println("👋 程序退出，再见!");
          return;
        }
        default -> System.out.println("❌ 无效选项，请重新输入");
      }
    }
  }

  private static void viewAllPosts() {
    List<MongoBlogPost> posts = datastore.find(MongoBlogPost.class).iterator().toList();
    if (posts.isEmpty()) {
      System.out.println("📭 暂无博客文章");
      return;
    }

    System.out.println("\n📝 所有博客文章 (共" + posts.size() + "篇):");
    for (int i = 0; i < posts.size(); i++) {
      MongoBlogPost p = posts.get(i);
      System.out.println((i+1) + ". " + p.getTitle() + " - " + p.getAuthor().getName() + " (" + p.getStatus() + ")");
    }
  }

  private static void addNewPost() {
    System.out.print("📝 请输入文章标题: ");
    String title = scanner.nextLine();
    System.out.print("📄 请输入文章内容: ");
    String content = scanner.nextLine();
    System.out.print("👤 请输入作者姓名: ");
    String authorName = scanner.nextLine();
    System.out.print("📧 请输入作者邮箱: ");
    String authorEmail = scanner.nextLine();
    System.out.print("🏷️ 请输入标签 (用逗号分隔): ");
    String tagsInput = scanner.nextLine();

    MongoBlogPost.Author author = new MongoBlogPost.Author(authorName, authorEmail, "用户添加的作者");
    List<String> tags = Arrays.asList(tagsInput.split(","));
    tags = tags.stream().map(String::trim).toList();

    MongoBlogPost newPost = new MongoBlogPost(title, content, author, tags, "draft");
    datastore.save(newPost);
    System.out.println("✅ 文章添加成功，ID: " + newPost.getId());
  }

  private static void searchByTag() {
    System.out.print("🔍 请输入要搜索的标签: ");
    String tag = scanner.nextLine();

    List<MongoBlogPost> posts = datastore.find(MongoBlogPost.class)
        .filter(Filters.in("tags", Arrays.asList(tag)))
        .iterator().toList();

    if (posts.isEmpty()) {
      System.out.println("📭 没有找到包含标签 '" + tag + "' 的文章");
    } else {
      System.out.println("\n🔍 包含标签 '" + tag + "' 的文章:");
      posts.forEach(p -> System.out.println("- " + p.getTitle()));
    }
  }

  private static void searchByAuthor() {
    System.out.print("👤 请输入作者姓名: ");
    String authorName = scanner.nextLine();

    List<MongoBlogPost> posts = datastore.find(MongoBlogPost.class)
        .filter(Filters.eq("author.name", authorName))
        .iterator().toList();

    if (posts.isEmpty()) {
      System.out.println("📭 没有找到作者 '" + authorName + "' 的文章");
    } else {
      System.out.println("\n👤 作者 '" + authorName + "' 的文章:");
      posts.forEach(p -> System.out.println("- " + p.getTitle()));
    }
  }

  private static void updatePostStatus() {
    viewAllPosts();
    System.out.print("\n请输入要更新的文章编号: ");
    try {
      int index = Integer.parseInt(scanner.nextLine()) - 1;
      List<MongoBlogPost> posts = datastore.find(MongoBlogPost.class).iterator().toList();

      if (index >= 0 && index < posts.size()) {
        MongoBlogPost post = posts.get(index);
        System.out.print("请输入新状态 (draft/published/archived): ");
        String newStatus = scanner.nextLine();

        post.setStatus(newStatus);
        datastore.save(post);
        System.out.println("✅ 文章状态已更新为: " + newStatus);
      } else {
        System.out.println("❌ 无效的文章编号");
      }
    } catch (NumberFormatException e) {
      System.out.println("❌ 请输入有效的数字");
    }
  }

  private static boolean confirmDelete() {
    System.out.print("⚠️ 确认删除所有数据吗？此操作不可恢复! (输入 'DELETE' 确认): ");
    return "DELETE".equals(scanner.nextLine());
  }
}