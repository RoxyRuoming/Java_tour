package org.example.nonsqlExamples;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RedisRunner {

  // Redis Docker容器信息 - 请根据你的实际情况修改
  private static final String CONTAINER_NAME = "redis"; // redis containner name
  private static final String REDIS_DB = "0"; // Redis db number，default - 0

  private static final Scanner scanner = new Scanner(System.in);

  public static void main(String[] args) {
    System.out.println("=== Java Redis CRUD 操作练习程序 ===");
    System.out.println("Redis容器: " + CONTAINER_NAME);
    System.out.println();

    // 测试连接
    if (!testConnection()) {
      System.err.println("❌ 无法连接到Redis容器");
      System.err.println("请检查: docker ps | grep redis");
      return;
    }

    System.out.println("✅ Redis连接测试成功！");

    // 主菜单循环
    while (true) {
      showMenu();
      int choice = getChoice();

      switch (choice) {
        case 1:
          demonstrateBasicCRUD();
          break;
        case 2:
          demonstrateStringOperations();
          break;
        case 3:
          demonstrateListOperations();
          break;
        case 4:
          demonstrateHashOperations();
          break;
        case 5:
          demonstrateSetOperations();
          break;
        case 6:
          executeCustomCommand();
          break;
        case 7:
          showRedisInfo();
          break;
        case 8:
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
    System.out.println("2. 📝 字符串操作演示");
    System.out.println("3. 📋 列表操作演示");
    System.out.println("4. 🗂️ 哈希操作演示");
    System.out.println("5. 🎯 集合操作演示");
    System.out.println("6. ✍️ 执行自定义Redis命令");
    System.out.println("7. 📊 查看Redis信息");
    System.out.println("8. 🗑️ 清空所有数据");
    System.out.println("0. 👋 退出程序");
    System.out.println("=".repeat(50));
    System.out.print("请输入选择 (0-8): ");
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
   * 测试Redis连接
   */
  private static boolean testConnection() {
    System.out.println("🔗 测试Redis连接...");
    return executeRedisCommand("PING"); // command design pattern
  }

  /**
   * 执行Redis命令的核心方法
   */
  private static boolean executeRedisCommand(String command) {
    return executeRedisCommand(command, true);
  }

  private static boolean executeRedisCommand(String command, boolean showOutput) {
    try {
      List<String> cmd = new ArrayList<>();
      cmd.add("docker");
      cmd.add("exec");
      cmd.add(CONTAINER_NAME);
      cmd.add("redis-cli"); //
      cmd.add("-n");
      cmd.add(REDIS_DB);

      // 如果命令包含空格，需要分割
      String[] commandParts = command.split(" ");
      for (String part : commandParts) {
        cmd.add(part);
      }

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
        System.err.println("❌ 执行Redis命令失败: " + e.getMessage());
      }
      return false;
    }
  }

  /**
   * 基本CRUD演示
   */
  private static void demonstrateBasicCRUD() {
    System.out.println("\n🚀 === 基本CRUD操作演示 ===");

    // CREATE - 设置键值对
    System.out.println("\n📝 CREATE - 设置数据:");
    System.out.println("命令: SET user:1:name \"张三\"");
    executeRedisCommand("SET user:1:name 张三");

    System.out.println("命令: SET user:1:age 25");
    executeRedisCommand("SET user:1:age 25");

    System.out.println("命令: SET user:1:email \"zhangsan@email.com\"");
    executeRedisCommand("SET user:1:email zhangsan@email.com");

    // READ - 读取数据
    System.out.println("\n🔍 READ - 读取数据:");
    System.out.println("命令: GET user:1:name");
    executeRedisCommand("GET user:1:name");

    System.out.println("命令: GET user:1:age");
    executeRedisCommand("GET user:1:age");

    System.out.println("命令: GET user:1:email");
    executeRedisCommand("GET user:1:email");

    // UPDATE - 更新数据
    System.out.println("\n✏️ UPDATE - 更新数据:");
    System.out.println("命令: SET user:1:age 26");
    executeRedisCommand("SET user:1:age 26");

    System.out.println("验证更新: GET user:1:age");
    executeRedisCommand("GET user:1:age");

    // DELETE - 删除数据
    System.out.println("\n🗑️ DELETE - 删除数据:");
    System.out.println("命令: DEL user:1:email");
    executeRedisCommand("DEL user:1:email");

    System.out.println("验证删除: GET user:1:email");
    executeRedisCommand("GET user:1:email");

    System.out.println("\n✅ 基本CRUD演示完成！");
  }

  /**
   * 字符串操作演示
   */
  private static void demonstrateStringOperations() {
    System.out.println("\n📝 === 字符串操作演示 ===");

    // 基本字符串操作
    System.out.println("\n🔢 数值操作:");
    System.out.println("命令: SET counter 10");
    executeRedisCommand("SET counter 10");

    System.out.println("命令: INCR counter");
    executeRedisCommand("INCR counter");

    System.out.println("命令: INCRBY counter 5");
    executeRedisCommand("INCRBY counter 5");

    System.out.println("命令: GET counter");
    executeRedisCommand("GET counter");

    // 字符串追加
    System.out.println("\n➕ 字符串追加:");
    System.out.println("命令: SET message \"Hello\"");
    executeRedisCommand("SET message Hello");

    System.out.println("命令: APPEND message \" World!\"");
    executeRedisCommand("APPEND message \" World!\"");

    System.out.println("命令: GET message");
    executeRedisCommand("GET message");

    // 设置过期时间
    System.out.println("\n⏰ 过期时间设置:");
    System.out.println("命令: SETEX temp_key 30 \"临时数据\"");
    executeRedisCommand("SETEX temp_key 30 临时数据");

    System.out.println("命令: TTL temp_key");
    executeRedisCommand("TTL temp_key");

    System.out.println("✅ 字符串操作演示完成！");
  }

  /**
   * 列表操作演示
   */
  private static void demonstrateListOperations() {
    System.out.println("\n📋 === 列表操作演示 ===");

    // 列表添加
    System.out.println("\n➕ 添加元素:");
    System.out.println("命令: LPUSH shopping_list 苹果");
    executeRedisCommand("LPUSH shopping_list 苹果");

    System.out.println("命令: LPUSH shopping_list 香蕉");
    executeRedisCommand("LPUSH shopping_list 香蕉");

    System.out.println("命令: RPUSH shopping_list 橘子");
    executeRedisCommand("RPUSH shopping_list 橘子");

    // 查看列表
    System.out.println("\n🔍 查看列表:");
    System.out.println("命令: LRANGE shopping_list 0 -1");
    executeRedisCommand("LRANGE shopping_list 0 -1");

    System.out.println("命令: LLEN shopping_list");
    executeRedisCommand("LLEN shopping_list");

    // 弹出元素
    System.out.println("\n📤 弹出元素:");
    System.out.println("命令: LPOP shopping_list");
    executeRedisCommand("LPOP shopping_list");

    System.out.println("查看剩余: LRANGE shopping_list 0 -1");
    executeRedisCommand("LRANGE shopping_list 0 -1");

    System.out.println("✅ 列表操作演示完成！");
  }

  /**
   * 哈希操作演示
   */
  private static void demonstrateHashOperations() {
    System.out.println("\n🗂️ === 哈希操作演示 ===");

    // 设置哈希字段
    System.out.println("\n📝 设置用户信息:");
    System.out.println("命令: HSET user:2 name 李四");
    executeRedisCommand("HSET user:2 name 李四");

    System.out.println("命令: HSET user:2 age 30");
    executeRedisCommand("HSET user:2 age 30");

    System.out.println("命令: HSET user:2 city 北京");
    executeRedisCommand("HSET user:2 city 北京");

    // 获取哈希字段
    System.out.println("\n🔍 获取信息:");
    System.out.println("命令: HGET user:2 name");
    executeRedisCommand("HGET user:2 name");

    System.out.println("命令: HGETALL user:2");
    executeRedisCommand("HGETALL user:2");

    // 更新字段
    System.out.println("\n✏️ 更新信息:");
    System.out.println("命令: HSET user:2 age 31");
    executeRedisCommand("HSET user:2 age 31");

    System.out.println("验证: HGET user:2 age");
    executeRedisCommand("HGET user:2 age");

    // 删除字段
    System.out.println("\n🗑️ 删除字段:");
    System.out.println("命令: HDEL user:2 city");
    executeRedisCommand("HDEL user:2 city");

    System.out.println("验证: HGETALL user:2");
    executeRedisCommand("HGETALL user:2");

    System.out.println("✅ 哈希操作演示完成！");
  }

  /**
   * 集合操作演示
   */
  private static void demonstrateSetOperations() {
    System.out.println("\n🎯 === 集合操作演示 ===");

    // 添加集合元素
    System.out.println("\n➕ 添加技能:");
    System.out.println("命令: SADD skills:java Java");
    executeRedisCommand("SADD skills:java Java");

    System.out.println("命令: SADD skills:java Python");
    executeRedisCommand("SADD skills:java Python");

    System.out.println("命令: SADD skills:java JavaScript");
    executeRedisCommand("SADD skills:java JavaScript");

    // 查看集合
    System.out.println("\n🔍 查看集合:");
    System.out.println("命令: SMEMBERS skills:java");
    executeRedisCommand("SMEMBERS skills:java");

    System.out.println("命令: SCARD skills:java");
    executeRedisCommand("SCARD skills:java");

    // 检查成员
    System.out.println("\n✅ 检查成员:");
    System.out.println("命令: SISMEMBER skills:java Java");
    executeRedisCommand("SISMEMBER skills:java Java");

    System.out.println("命令: SISMEMBER skills:java PHP");
    executeRedisCommand("SISMEMBER skills:java PHP");

    // 删除成员
    System.out.println("\n🗑️ 删除成员:");
    System.out.println("命令: SREM skills:java JavaScript");
    executeRedisCommand("SREM skills:java JavaScript");

    System.out.println("验证: SMEMBERS skills:java");
    executeRedisCommand("SMEMBERS skills:java");

    System.out.println("✅ 集合操作演示完成！");
  }

  /**
   * 执行自定义Redis命令
   */
  private static void executeCustomCommand() {
    System.out.println("\n✍️ 自定义Redis命令执行模式");
    System.out.println("💡 提示: 输入 'help' 查看常用命令，输入 'quit' 退出");

    while (true) {
      System.out.print("\nRedis> ");
      String command = scanner.nextLine().trim();

      if ("quit".equalsIgnoreCase(command)) {
        System.out.println("👋 退出自定义命令模式");
        break;
      }

      if ("help".equalsIgnoreCase(command)) {
        showRedisHelp();
        continue;
      }

      if (!command.isEmpty()) {
        System.out.println("🔍 执行: " + command);
        executeRedisCommand(command);
      }
    }
  }

  /**
   * 显示Redis帮助信息
   */
  private static void showRedisHelp() {
    System.out.println("\n💡 常用Redis命令:");
    System.out.println("字符串: SET key value, GET key, DEL key");
    System.out.println("列表: LPUSH list item, RPUSH list item, LRANGE list 0 -1");
    System.out.println("哈希: HSET hash field value, HGET hash field, HGETALL hash");
    System.out.println("集合: SADD set member, SMEMBERS set, SREM set member");
    System.out.println("通用: KEYS *, EXISTS key, TYPE key, TTL key");
  }

  /**
   * 显示Redis信息
   */
  private static void showRedisInfo() {
    System.out.println("\n📊 === Redis信息 ===");

    System.out.println("🔍 数据库大小:");
    System.out.println("命令: DBSIZE");
    executeRedisCommand("DBSIZE");

    System.out.println("\n🔑 所有键 (最多显示20个):");
    System.out.println("命令: KEYS *");
    executeRedisCommand("KEYS *");

    System.out.println("\n💾 内存使用:");
    System.out.println("命令: INFO memory");
    executeRedisCommand("INFO memory");
  }

  /**
   * 清空所有数据
   */
  private static void clearAllData() {
    System.out.println("\n🗑️ === 清空数据 ===");
    System.out.print("⚠️ 确定要清空所有数据吗？(输入 'yes' 确认): ");
    String confirm = scanner.nextLine().trim();

    if ("yes".equalsIgnoreCase(confirm)) {
      System.out.println("命令: FLUSHDB");
      executeRedisCommand("FLUSHDB");
      System.out.println("✅ 数据清空完成");
    } else {
      System.out.println("❌ 操作已取消");
    }
  }
}