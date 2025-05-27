package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MongoDBRunner {

  // MongoDB Docker容器信息 - 请根据你的实际情况修改
  private static final String CONTAINER_NAME = "mongodb"; // MongoDB container name
  private static final String DATABASE_NAME = "testdb"; // MongoDB database name (会自动创建)
  private static final String MONGO_USERNAME = "admin"; // MongoDB用户名
  private static final String MONGO_PASSWORD = "admin123"; // MongoDB密码

  private static final Scanner scanner = new Scanner(System.in);

  public static void main(String[] args) {
    System.out.println("=== Java MongoDB CRUD 操作练习程序 ===");
    System.out.println("MongoDB容器: " + CONTAINER_NAME);
    System.out.println();

    // 测试连接
    if (!testConnection()) {
      System.err.println("❌ 无法连接到MongoDB容器");
      System.err.println("请检查: docker ps | grep mongodb");
      return;
    }

    System.out.println("✅ MongoDB连接测试成功！");

    // 主菜单循环
    while (true) {
      showMenu();
      int choice = getChoice();

      switch (choice) {
        case 1:
          demonstrateBasicCRUD();
          break;
        case 2:
          demonstrateCollectionOperations();
          break;
        case 3:
          demonstrateQueryOperations();
          break;
        case 4:
          demonstrateUpdateOperations();
          break;
        case 5:
          executeCustomCommand();
          break;
        case 6:
          showMongoInfo();
          break;
        case 7:
          clearAllData();
          break;
        case 0:
          System.out.println("👋 程序退出，再见！");
          scanner.close();
          return;
        default:
          System.out.println("❌ 无效选择，请重新输入");
      }

      System.out.println("\n按回车键继续...");
      scanner.nextLine();
    }
  }

  /**
   * 显示主菜单
   */
  private static void showMenu() {
    System.out.println("\n" + "=".repeat(50));
    System.out.println("🎯 选择操作:");
    System.out.println("1. 🚀 基本CRUD演示");
    System.out.println("2. 📁 集合操作演示");
    System.out.println("3. 🔍 查询操作演示");
    System.out.println("4. ✏️ 更新操作演示");
    System.out.println("5. ✍️ 执行自定义MongoDB命令");
    System.out.println("6. 📊 查看MongoDB信息");
    System.out.println("7. 🗑️ 清空所有数据");
    System.out.println("0. 👋 退出程序");
    System.out.println("=".repeat(50));
    System.out.print("请输入选择 (0-7): ");
  }

  /**
   * 获取用户选择
   */
  private static int getChoice() {
    try {
      String input = scanner.nextLine().trim();
      return Integer.parseInt(input);
    } catch (NumberFormatException e) {
      return -1;
    }
  }

  /**
   * 测试MongoDB连接
   */
  private static boolean testConnection() {
    System.out.println("🔗 测试MongoDB连接...");
    return executeMongoCommand("db.runCommand({ping: 1})");
  }

  /**
   * 执行MongoDB命令的核心方法
   */
  private static boolean executeMongoCommand(String command) {
    return executeMongoCommand(command, true);
  }

  private static boolean executeMongoCommand(String command, boolean showOutput) {
    try {
      List<String> cmd = new ArrayList<>();
      cmd.add("docker");
      cmd.add("exec");
      cmd.add(CONTAINER_NAME);
      cmd.add("mongosh");
      cmd.add("--quiet");
      cmd.add("-u");
      cmd.add(MONGO_USERNAME);
      cmd.add("-p");
      cmd.add(MONGO_PASSWORD);
      cmd.add("--authenticationDatabase");
      cmd.add("admin");
      cmd.add(DATABASE_NAME);
      cmd.add("--eval");
      cmd.add(command);

      ProcessBuilder pb = new ProcessBuilder(cmd);
      pb.redirectErrorStream(true);
      Process process = pb.start();

      boolean hasOutput = false;
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
        String line;
        while ((line = reader.readLine()) != null) {
          if (showOutput) {
            System.out.println(line);
          }
          hasOutput = true;
        }
      }

      int exitCode = process.waitFor();

      if (!hasOutput && exitCode == 0 && showOutput) {
        System.out.println("(命令执行成功，无输出)");
      }

      return exitCode == 0;

    } catch (IOException | InterruptedException e) {
      if (showOutput) {
        System.err.println("❌ 执行MongoDB命令失败: " + e.getMessage());
      }
      return false;
    }
  }

  /**
   * 基本CRUD演示 - MongoDB特色：复杂嵌套文档
   */
  private static void demonstrateBasicCRUD() {
    System.out.println("\n🚀 === MongoDB文档特性CRUD演示 ===");

    // CREATE - 插入复杂嵌套文档 (体现MongoDB文档数据库特性)
    System.out.println("\n📝 CREATE - 插入复杂嵌套文档:");
    System.out.println("插入电商用户文档 (包含嵌套地址、订单历史、偏好设置):");

    String complexUser = "db.users.insertOne({" +
        "name: '张三', " +
        "age: 25, " +
        "email: 'zhangsan@email.com', " +
        "profile: {" +
        "  avatar: 'https://example.com/avatar1.jpg', " +
        "  bio: '热爱编程的软件工程师', " +
        "  preferences: {" +
        "    language: 'zh-CN', " +
        "    theme: 'dark', " +
        "    notifications: {email: true, sms: false}" +
        "  }" +
        "}, " +
        "addresses: [" +
        "  {type: 'home', city: '北京', district: '朝阳区', street: '望京SOHO', zipcode: '100000'}, " +
        "  {type: 'work', city: '北京', district: '海淀区', street: '中关村大街', zipcode: '100080'}" +
        "], " +
        "skills: ['Java', 'Python', 'MongoDB', 'Docker'], " +
        "orderHistory: [" +
        "  {orderId: 'ORD001', amount: 299.99, date: new Date('2024-01-15'), status: 'completed'}, " +
        "  {orderId: 'ORD002', amount: 89.50, date: new Date('2024-01-20'), status: 'shipped'}" +
        "], " +
        "metadata: {" +
        "  createdAt: new Date(), " +
        "  lastLoginAt: new Date(), " +
        "  loginCount: 42" +
        "}" +
        "})";

    executeMongoCommand(complexUser);

    // 插入博客文章文档
    System.out.println("\n📄 插入博客文章文档 (展示数组和文本搜索):");
    String blogPost = "db.posts.insertOne({" +
        "title: 'MongoDB最佳实践指南', " +
        "content: '本文介绍MongoDB在实际项目中的应用技巧和最佳实践...', " +
        "author: {" +
        "  name: '张三', " +
        "  email: 'zhangsan@email.com'" +
        "}, " +
        "tags: ['MongoDB', 'NoSQL', '数据库', '最佳实践'], " +
        "categories: ['技术', '数据库'], " +
        "stats: {" +
        "  views: 1250, " +
        "  likes: 89, " +
        "  comments: 23" +
        "}, " +
        "comments: [" +
        "  {user: '李四', content: '写得很好！', date: new Date()}, " +
        "  {user: '王五', content: '学到了很多', date: new Date()}" +
        "], " +
        "publishedAt: new Date(), " +
        "status: 'published'" +
        "})";

    executeMongoCommand(blogPost);

    // READ - 复杂查询 (体现MongoDB查询能力)
    System.out.println("\n🔍 READ - MongoDB特色查询:");

    System.out.println("1. 查询嵌套字段 - 偏好为深色主题的用户:");
    executeMongoCommand("db.users.find({'profile.preferences.theme': 'dark'}).pretty()");

    System.out.println("2. 数组查询 - 掌握Java技能的用户:");
    executeMongoCommand("db.users.find({skills: 'Java'}).pretty()");

    System.out.println("3. 投影查询 - 只返回姓名和技能:");
    executeMongoCommand("db.users.find({}, {name: 1, skills: 1, _id: 0}).pretty()");

    // UPDATE - MongoDB特色更新操作
    System.out.println("\n✏️ UPDATE - MongoDB特色更新:");

    System.out.println("1. 数组添加元素 - 添加新技能:");
    executeMongoCommand("db.users.updateOne({name: '张三'}, {$push: {skills: 'Kubernetes'}})");

    System.out.println("2. 嵌套文档更新 - 更新偏好设置:");
    executeMongoCommand("db.users.updateOne({name: '张三'}, {$set: {'profile.preferences.theme': 'light'}})");

    System.out.println("3. 数组元素更新 - 更新订单状态:");
    executeMongoCommand("db.users.updateOne({'orderHistory.orderId': 'ORD002'}, {$set: {'orderHistory.$.status': 'delivered'}})");

    System.out.println("验证更新: ");
    executeMongoCommand("db.users.findOne({name: '张三'})");

    System.out.println("\n✅ MongoDB文档特性CRUD演示完成！");
  }

  /**
   * 集合操作演示
   */
  private static void demonstrateCollectionOperations() {
    System.out.println("\n📁 === 集合操作演示 ===");

    // 创建集合
    System.out.println("\n➕ 创建集合:");
    System.out.println("命令: db.createCollection('products')");
    executeMongoCommand("db.createCollection('products')");

    // 插入产品数据
    System.out.println("\n📦 插入产品数据:");
    executeMongoCommand("db.products.insertMany([" +
        "{name: '笔记本电脑', price: 5000, category: '电子产品'}," +
        "{name: '鼠标', price: 50, category: '电子产品'}," +
        "{name: '键盘', price: 200, category: '电子产品'}" +
        "])");

    // 查看集合
    System.out.println("\n🔍 查看集合:");
    System.out.println("命令: show collections");
    executeMongoCommand("db.runCommand('listCollections').cursor.firstBatch.forEach(printjson)");

    System.out.println("命令: db.products.find()");
    executeMongoCommand("db.products.find().pretty()");

    System.out.println("✅ 集合操作演示完成！");
  }

  /**
   * 查询操作演示
   */
  private static void demonstrateQueryOperations() {
    System.out.println("\n🔍 === 查询操作演示 ===");

    // 条件查询
    System.out.println("\n🎯 条件查询:");
    System.out.println("命令: db.products.find({category: '电子产品'})");
    executeMongoCommand("db.products.find({category: '电子产品'}).pretty()");

    System.out.println("命令: db.products.find({price: {$gt: 100}})");
    executeMongoCommand("db.products.find({price: {$gt: 100}}).pretty()");

    // 排序和限制
    System.out.println("\n📊 排序和限制:");
    System.out.println("命令: db.products.find().sort({price: -1})");
    executeMongoCommand("db.products.find().sort({price: -1}).pretty()");

    System.out.println("命令: db.products.find().limit(2)");
    executeMongoCommand("db.products.find().limit(2).pretty()");

    // 统计
    System.out.println("\n📈 统计操作:");
    System.out.println("命令: db.products.countDocuments()");
    executeMongoCommand("db.products.countDocuments()");

    System.out.println("✅ 查询操作演示完成！");
  }

  /**
   * 更新操作演示
   */
  private static void demonstrateUpdateOperations() {
    System.out.println("\n✏️ === 更新操作演示 ===");

    // 单个更新
    System.out.println("\n🔧 单个更新:");
    System.out.println("命令: db.products.updateOne({name: '鼠标'}, {$set: {price: 60, stock: 100}})");
    executeMongoCommand("db.products.updateOne({name: '鼠标'}, {$set: {price: 60, stock: 100}})");

    // 批量更新
    System.out.println("\n🔧 批量更新:");
    System.out.println("命令: db.products.updateMany({category: '电子产品'}, {$set: {available: true}})");
    executeMongoCommand("db.products.updateMany({category: '电子产品'}, {$set: {available: true}})");

    // 替换文档
    System.out.println("\n🔄 替换文档:");
    System.out.println("命令: db.products.replaceOne({name: '键盘'}, {name: '机械键盘', price: 300, category: '电子产品', type: '机械'})");
    executeMongoCommand("db.products.replaceOne({name: '键盘'}, {name: '机械键盘', price: 300, category: '电子产品', type: '机械'})");

    System.out.println("\n验证更新: db.products.find()");
    executeMongoCommand("db.products.find().pretty()");

    System.out.println("✅ 更新操作演示完成！");
  }

  /**
   * 执行自定义MongoDB命令
   */
  private static void executeCustomCommand() {
    System.out.println("\n✍️ 自定义MongoDB命令执行模式");
    System.out.println("💡 提示: 输入 'help' 查看常用命令，输入 'quit' 退出");

    while (true) {
      System.out.print("\nMongoDB> ");
      String command = scanner.nextLine().trim();

      if ("quit".equalsIgnoreCase(command)) {
        System.out.println("👋 退出自定义命令模式");
        break;
      }

      if ("help".equalsIgnoreCase(command)) {
        showMongoHelp();
        continue;
      }

      if (!command.isEmpty()) {
        System.out.println("🔍 执行: " + command);
        executeMongoCommand(command);
      }
    }
  }

  /**
   * 显示MongoDB帮助信息
   */
  private static void showMongoHelp() {
    System.out.println("\n💡 常用MongoDB命令:");
    System.out.println("插入: db.collection.insertOne({field: value})");
    System.out.println("查询: db.collection.find({condition})");
    System.out.println("更新: db.collection.updateOne({condition}, {$set: {field: value}})");
    System.out.println("删除: db.collection.deleteOne({condition})");
    System.out.println("统计: db.collection.countDocuments()");
    System.out.println("集合: show collections");
  }

  /**
   * 显示MongoDB信息
   */
  private static void showMongoInfo() {
    System.out.println("\n📊 === MongoDB信息 ===");

    System.out.println("📊 数据库统计:");
    System.out.println("命令: db.stats()");
    executeMongoCommand("db.stats()");

    System.out.println("\n📁 所有集合:");
    System.out.println("命令: show collections");
    executeMongoCommand("db.runCommand('listCollections').cursor.firstBatch.forEach(printjson)");

    System.out.println("\n🔢 文档计数:");
    executeMongoCommand("db.users.countDocuments()");
    executeMongoCommand("db.products.countDocuments()");
  }

  /**
   * 清空所有数据
   */
  private static void clearAllData() {
    System.out.println("\n🗑️ === 清空数据 ===");
    System.out.print("⚠️ 确定要清空所有数据吗？(输入 'yes' 确认): ");
    String confirm = scanner.nextLine().trim();

    if ("yes".equalsIgnoreCase(confirm)) {
      System.out.println("命令: db.dropDatabase()");
      executeMongoCommand("db.dropDatabase()");
      System.out.println("✅ 数据清空完成");
    } else {
      System.out.println("❌ 操作已取消");
    }
  }
}