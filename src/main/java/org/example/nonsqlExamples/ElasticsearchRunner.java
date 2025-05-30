package org.example.nonsqlExamples;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ElasticsearchRunner {

  // Elasticsearch Docker容器信息 - 请根据你的实际情况修改
  private static final String CONTAINER_NAME = "elasticsearch"; // Elasticsearch container name
  private static final String ES_URL = "http://localhost:9200"; // Elasticsearch URL
  // 注意：你的ES配置已禁用安全认证 xpack.security.enabled=false，无需用户名密码

  private static final Scanner scanner = new Scanner(System.in);

  public static void main(String[] args) {
    System.out.println("=== Java Elasticsearch CRUD 操作练习程序 ===");
    System.out.println("Elasticsearch容器: " + CONTAINER_NAME);
    System.out.println();

    // 测试连接
    if (!testConnection()) {
      System.err.println("❌ 无法连接到Elasticsearch容器");
      System.err.println("请检查: docker ps | grep elasticsearch");
      return;
    }

    System.out.println("✅ Elasticsearch连接测试成功！");

    // 主菜单循环
    while (true) {
      showMenu();
      int choice = getChoice();

      switch (choice) {
        case 1:
          demonstrateBasicCRUD();
          break;
        case 2:
          demonstrateIndexOperations();
          break;
        case 3:
          demonstrateSearchOperations();
          break;
        case 4:
          demonstrateBulkOperations();
          break;
        case 5:
          executeCustomCommand();
          break;
        case 6:
          showElasticsearchInfo();
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
    System.out.println("2. 📑 索引操作演示");
    System.out.println("3. 🔍 搜索操作演示");
    System.out.println("4. 📦 批量操作演示");
    System.out.println("5. ✍️ 执行自定义Elasticsearch命令");
    System.out.println("6. 📊 查看Elasticsearch信息");
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
   * 测试Elasticsearch连接
   */
  private static boolean testConnection() {
    System.out.println("🔗 测试Elasticsearch连接...");
    return executeCurlCommand("GET", "");
  }

  /**
   * 执行curl命令的核心方法
   */
  private static boolean executeCurlCommand(String method, String endpoint) {
    return executeCurlCommand(method, endpoint, "", true);
  }

  private static boolean executeCurlCommand(String method, String endpoint, String data, boolean showOutput) {
    try {
      List<String> cmd = new ArrayList<>();
      cmd.add("docker");
      cmd.add("exec");
      cmd.add(CONTAINER_NAME);
      cmd.add("curl");
      cmd.add("-s");
      cmd.add("-X");
      cmd.add(method);

      if (!data.isEmpty()) {
        cmd.add("-H");
        cmd.add("Content-Type: application/json");
        cmd.add("-d");
        cmd.add(data);
      }

      cmd.add(ES_URL + "/" + endpoint);

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
        System.err.println("❌ 执行Elasticsearch命令失败: " + e.getMessage());
      }
      return false;
    }
  }

  /**
   * 基本CRUD演示 - Elasticsearch特色：全文搜索和分析
   */
  private static void demonstrateBasicCRUD() {
    System.out.println("\n🚀 === Elasticsearch搜索引擎特性CRUD演示 ===");

    // CREATE - 创建带有复杂映射的文档 (体现ES搜索引擎特性)
    System.out.println("\n📝 CREATE - 创建文档 (针对搜索优化):");

    // 创建博客文章索引，专为全文搜索设计
    System.out.println("1. 创建技术博客文章 (包含全文搜索字段):");
    String article1 = "{" +
        "\"title\":\"深入理解Java虚拟机：JVM内存管理与垃圾回收机制\"," +
        "\"content\":\"Java虚拟机(JVM)是Java程序运行的核心，它负责将Java字节码转换为机器码执行。JVM的内存管理包括堆内存、栈内存、方法区等。垃圾回收(GC)是JVM自动内存管理的重要机制，包括标记-清除、复制算法、标记-整理等多种算法。\"," +
        "\"author\":\"张三\"," +
        "\"tags\":[\"Java\",\"JVM\",\"垃圾回收\",\"内存管理\",\"性能优化\"]," +
        "\"category\":\"技术分享\"," +
        "\"difficulty\":\"高级\"," +
        "\"readTime\":15," +
        "\"views\":2500," +
        "\"likes\":89," +
        "\"publishDate\":\"2024-01-15T10:30:00\"," +
        "\"updateDate\":\"2024-01-16T09:15:00\"," +
        "\"status\":\"published\"," +
        "\"metadata\":{" +
        "  \"wordCount\":3500," +
        "  \"language\":\"zh-CN\"," +
        "  \"seoKeywords\":[\"Java性能\",\"JVM调优\",\"内存优化\"]" +
        "}" +
        "}";
    executeCurlCommand("POST", "tech_blog/_doc", article1, true);

    System.out.println("2. 创建产品评论文档 (用于情感分析和搜索):");
    String review1 = "{" +
        "\"productId\":\"PROD001\"," +
        "\"productName\":\"MacBook Pro 16英寸\"," +
        "\"reviewTitle\":\"性能卓越的专业工作站\"," +
        "\"reviewContent\":\"这款MacBook Pro的M2芯片性能令人印象深刻，编译大型Java项目的速度比之前的Intel版本快了近50%。屏幕色彩还原度极高，适合设计工作。电池续航在重度使用下也能坚持8小时以上。唯一的不足是价格偏高，但考虑到性能提升，还是物有所值的。\"," +
        "\"rating\":4.5," +
        "\"sentiment\":\"positive\"," +
        "\"reviewer\":{" +
        "  \"name\":\"李四\"," +
        "  \"level\":\"专业用户\"," +
        "  \"verifiedPurchase\":true" +
        "}," +
        "\"categories\":[\"笔记本电脑\",\"苹果产品\",\"专业设备\"]," +
        "\"features\":[\"性能\",\"屏幕\",\"电池\",\"价格\"]," +
        "\"reviewDate\":\"2024-01-20T14:30:00\"," +
        "\"helpfulVotes\":23," +
        "\"totalVotes\":25" +
        "}";
    executeCurlCommand("POST", "product_reviews/_doc", review1, true);

    // READ - Elasticsearch特色搜索
    System.out.println("\n🔍 READ - Elasticsearch特色搜索:");

    System.out.println("1. 全文搜索 - 搜索包含'Java性能'的文章:");
    String fullTextSearch = "{\"query\":{\"multi_match\":{\"query\":\"Java性能\",\"fields\":[\"title^2\",\"content\",\"tags\"]}},\"highlight\":{\"fields\":{\"title\":{},\"content\":{}}}}";
    executeCurlCommand("GET", "tech_blog/_search", fullTextSearch, true);

    System.out.println("2. 复合查询 - 高级Java文章，浏览量>1000:");
    String complexQuery = "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"tags\":\"Java\"}},{\"term\":{\"difficulty\":\"高级\"}},{\"range\":{\"views\":{\"gt\":1000}}}]}},\"sort\":[{\"views\":{\"order\":\"desc\"}}]}";
    executeCurlCommand("GET", "tech_blog/_search", complexQuery, true);

    System.out.println("3. 聚合分析 - 按分类统计文章数量和平均浏览量:");
    String aggregationQuery = "{\"size\":0,\"aggs\":{\"category_stats\":{\"terms\":{\"field\":\"category.keyword\"},\"aggs\":{\"avg_views\":{\"avg\":{\"field\":\"views\"}},\"total_likes\":{\"sum\":{\"field\":\"likes\"}}}}}}";
    executeCurlCommand("GET", "tech_blog/_search", aggregationQuery, true);

    // UPDATE - 智能更新
    System.out.println("\n✏️ UPDATE - 基于搜索的智能更新:");

    System.out.println("1. 更新文章统计 (模拟用户交互):");
    String updateStats = "{\"script\":{\"source\":\"ctx._source.views += params.view_increment; ctx._source.likes += params.like_increment\",\"params\":{\"view_increment\":100,\"like_increment\":5}}}";
    executeCurlCommand("POST", "tech_blog/_update_by_query", updateStats, true);

    // DELETE - 基于查询的删除
    System.out.println("\n🗑️ DELETE - 基于条件的批量删除:");
    System.out.println("删除浏览量低于100的草稿文章 (演示概念，不实际执行)");
    System.out.println("查询语法: DELETE /tech_blog/_doc/_query?q=status:draft AND views:<100");

    System.out.println("\n✅ Elasticsearch搜索引擎特性CRUD演示完成！");
  }

  /**
   * 索引操作演示
   */
  private static void demonstrateIndexOperations() {
    System.out.println("\n📑 === 索引操作演示 ===");

    // 创建索引
    System.out.println("\n➕ 创建索引:");
    String indexMapping = "{\"mappings\":{\"properties\":{\"title\":{\"type\":\"text\"},\"content\":{\"type\":\"text\"},\"tags\":{\"type\":\"keyword\"},\"created_at\":{\"type\":\"date\"}}}}";
    System.out.println("命令: PUT /articles");
    executeCurlCommand("PUT", "articles", indexMapping, true);

    // 添加文档到新索引
    System.out.println("\n📄 添加文档:");
    String article1 = "{\"title\":\"Java编程入门\",\"content\":\"Java是一种面向对象的编程语言\",\"tags\":[\"Java\",\"编程\"],\"created_at\":\"2024-01-01\"}";
    executeCurlCommand("POST", "articles/_doc", article1, true);

    String article2 = "{\"title\":\"Python数据分析\",\"content\":\"Python在数据科学领域应用广泛\",\"tags\":[\"Python\",\"数据分析\"],\"created_at\":\"2024-01-02\"}";
    executeCurlCommand("POST", "articles/_doc", article2, true);

    // 查看索引信息
    System.out.println("\n📊 查看索引信息:");
    System.out.println("命令: GET /articles");
    executeCurlCommand("GET", "articles");

    System.out.println("命令: GET /_cat/indices");
    executeCurlCommand("GET", "_cat/indices?v");

    System.out.println("✅ 索引操作演示完成！");
  }

  /**
   * 搜索操作演示
   */
  private static void demonstrateSearchOperations() {
    System.out.println("\n🔍 === 搜索操作演示 ===");

    // 全文搜索
    System.out.println("\n🔎 全文搜索:");
    String searchQuery1 = "{\"query\":{\"match\":{\"content\":\"Java\"}}}";
    System.out.println("命令: GET /articles/_search (搜索包含'Java'的文章)");
    executeCurlCommand("GET", "articles/_search", searchQuery1, true);

    // 精确匹配
    System.out.println("\n🎯 精确匹配:");
    String searchQuery2 = "{\"query\":{\"term\":{\"tags\":\"Python\"}}}";
    System.out.println("命令: GET /articles/_search (标签精确匹配'Python')");
    executeCurlCommand("GET", "articles/_search", searchQuery2, true);

    // 范围查询
    System.out.println("\n📅 范围查询:");
    String searchQuery3 = "{\"query\":{\"range\":{\"created_at\":{\"gte\":\"2024-01-01\",\"lte\":\"2024-01-31\"}}}}";
    System.out.println("命令: GET /articles/_search (日期范围查询)");
    executeCurlCommand("GET", "articles/_search", searchQuery3, true);

    // 聚合查询
    System.out.println("\n📈 聚合查询:");
    String aggQuery = "{\"aggs\":{\"tags_count\":{\"terms\":{\"field\":\"tags\"}}}}";
    System.out.println("命令: GET /articles/_search (标签聚合统计)");
    executeCurlCommand("GET", "articles/_search", aggQuery, true);

    System.out.println("✅ 搜索操作演示完成！");
  }

  /**
   * 批量操作演示
   */
  private static void demonstrateBulkOperations() {
    System.out.println("\n📦 === 批量操作演示 ===");

    // 批量插入
    System.out.println("\n➕ 批量插入:");
    String bulkData = "{\"index\":{\"_index\":\"products\",\"_id\":\"1\"}}\n" +
        "{\"name\":\"笔记本电脑\",\"price\":5000,\"category\":\"电子产品\"}\n" +
        "{\"index\":{\"_index\":\"products\",\"_id\":\"2\"}}\n" +
        "{\"name\":\"智能手机\",\"price\":3000,\"category\":\"电子产品\"}\n" +
        "{\"index\":{\"_index\":\"products\",\"_id\":\"3\"}}\n" +
        "{\"name\":\"平板电脑\",\"price\":2000,\"category\":\"电子产品\"}\n";

    System.out.println("命令: POST /_bulk");
    executeCurlCommand("POST", "_bulk", bulkData, true);

    // 批量更新
    System.out.println("\n✏️ 批量更新:");
    String bulkUpdate = "{\"update\":{\"_index\":\"products\",\"_id\":\"1\"}}\n" +
        "{\"doc\":{\"price\":4800,\"discount\":true}}\n" +
        "{\"update\":{\"_index\":\"products\",\"_id\":\"2\"}}\n" +
        "{\"doc\":{\"price\":2800,\"discount\":true}}\n";

    executeCurlCommand("POST", "_bulk", bulkUpdate, true);

    // 查看结果
    System.out.println("\n🔍 查看批量操作结果:");
    System.out.println("命令: GET /products/_search");
    executeCurlCommand("GET", "products/_search");

    System.out.println("✅ 批量操作演示完成！");
  }

  /**
   * 执行自定义Elasticsearch命令
   */
  private static void executeCustomCommand() {
    System.out.println("\n✍️ 自定义Elasticsearch命令执行模式");
    System.out.println("💡 提示: 输入格式 'METHOD /endpoint [data]'，输入 'help' 查看常用命令，输入 'quit' 退出");

    while (true) {
      System.out.print("\nElasticsearch> ");
      String input = scanner.nextLine().trim();

      if ("quit".equalsIgnoreCase(input)) {
        System.out.println("👋 退出自定义命令模式");
        break;
      }

      if ("help".equalsIgnoreCase(input)) {
        showElasticsearchHelp();
        continue;
      }

      if (!input.isEmpty()) {
        parseAndExecuteCommand(input);
      }
    }
  }

  /**
   * 解析并执行用户输入的命令
   */
  private static void parseAndExecuteCommand(String input) {
    String[] parts = input.split(" ", 3);
    if (parts.length < 2) {
      System.out.println("❌ 格式错误，请使用: METHOD /endpoint [data]");
      return;
    }

    String method = parts[0].toUpperCase();
    String endpoint = parts[1].startsWith("/") ? parts[1].substring(1) : parts[1];
    String data = parts.length > 2 ? parts[2] : "";

    System.out.println("🔍 执行: " + method + " " + endpoint);
    executeCurlCommand(method, endpoint, data, true);
  }

  /**
   * 显示Elasticsearch帮助信息
   */
  private static void showElasticsearchHelp() {
    System.out.println("\n💡 常用Elasticsearch命令格式:");
    System.out.println("创建文档: PUT /index/_doc/id {\"field\":\"value\"}");
    System.out.println("获取文档: GET /index/_doc/id");
    System.out.println("搜索: GET /index/_search {\"query\":{\"match\":{\"field\":\"value\"}}}");
    System.out.println("更新文档: POST /index/_update/id {\"doc\":{\"field\":\"new_value\"}}");
    System.out.println("删除文档: DELETE /index/_doc/id");
    System.out.println("查看索引: GET /_cat/indices");
  }

  /**
   * 显示Elasticsearch信息
   */
  private static void showElasticsearchInfo() {
    System.out.println("\n📊 === Elasticsearch信息 ===");

    System.out.println("🏥 集群健康状态:");
    System.out.println("命令: GET /_cluster/health");
    executeCurlCommand("GET", "_cluster/health");

    System.out.println("\n📑 所有索引:");
    System.out.println("命令: GET /_cat/indices");
    executeCurlCommand("GET", "_cat/indices?v");

    System.out.println("\n📊 节点信息:");
    System.out.println("命令: GET /_cat/nodes");
    executeCurlCommand("GET", "_cat/nodes?v");

    System.out.println("\n💾 存储统计:");
    System.out.println("命令: GET /_stats");
    executeCurlCommand("GET", "_stats/store,docs");
  }

  /**
   * 清空所有数据
   */
  private static void clearAllData() {
    System.out.println("\n🗑️ === 清空数据 ===");
    System.out.print("⚠️ 确定要清空所有索引吗？(输入 'yes' 确认): ");
    String confirm = scanner.nextLine().trim();

    if ("yes".equalsIgnoreCase(confirm)) {
      System.out.println("命令: DELETE /*");
      executeCurlCommand("DELETE", "*");
      System.out.println("✅ 所有索引已清空");
    } else {
      System.out.println("❌ 操作已取消");
    }
  }
}