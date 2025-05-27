package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CassandraRunner {

  // Cassandra Docker容器信息 - 请根据你的实际情况修改
  private static final String CONTAINER_NAME = "cassandra"; // Cassandra container name
  private static final String KEYSPACE_NAME = "testks"; // Cassandra keyspace name
  private static final String CLUSTER_NAME = "Test Cluster"; // 集群名称（匹配docker-compose配置）

  private static final Scanner scanner = new Scanner(System.in);

  public static void main(String[] args) {
    System.out.println("=== Java Cassandra CRUD 操作练习程序 ===");
    System.out.println("Cassandra容器: " + CONTAINER_NAME);
    System.out.println();

    // 测试连接
    if (!testConnection()) {
      System.err.println("❌ 无法连接到Cassandra容器");
      System.err.println("请检查: docker ps | grep cassandra");
      return;
    }

    System.out.println("✅ Cassandra连接测试成功！");

    // 初始化keyspace和表
    initializeKeyspaceAndTables();

    // 主菜单循环
    while (true) {
      showMenu();
      int choice = getChoice();

      switch (choice) {
        case 1:
          demonstrateBasicCRUD();
          break;
        case 2:
          demonstrateKeyspaceOperations();
          break;
        case 3:
          demonstrateTableOperations();
          break;
        case 4:
          demonstrateQueryOperations();
          break;
        case 5:
          executeCustomCommand();
          break;
        case 6:
          showCassandraInfo();
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
    System.out.println("2. 🏠 Keyspace操作演示");
    System.out.println("3. 📋 表操作演示");
    System.out.println("4. 🔍 查询操作演示");
    System.out.println("5. ✍️ 执行自定义CQL命令");
    System.out.println("6. 📊 查看Cassandra信息");
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
   * 测试Cassandra连接
   */
  private static boolean testConnection() {
    System.out.println("🔗 测试Cassandra连接...");
    return executeCQLCommand("SELECT now() FROM system.local;");
  }

  /**
   * 执行CQL命令的核心方法
   */
  private static boolean executeCQLCommand(String command) {
    return executeCQLCommand(command, true);
  }

  private static boolean executeCQLCommand(String command, boolean showOutput) {
    try {
      List<String> cmd = new ArrayList<>();
      cmd.add("docker");
      cmd.add("exec");
      cmd.add(CONTAINER_NAME);
      cmd.add("cqlsh");
      cmd.add("-e");
      cmd.add(command);

      ProcessBuilder pb = new ProcessBuilder(cmd);
      pb.redirectErrorStream(true);
      Process process = pb.start();

      boolean hasOutput = false;
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
        String line;
        while ((line = reader.readLine()) != null) {
          if (showOutput && !line.trim().isEmpty()) {
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
        System.err.println("❌ 执行CQL命令失败: " + e.getMessage());
      }
      return false;
    }
  }

  /**
   * 初始化Keyspace和表
   */
  private static void initializeKeyspaceAndTables() {
    System.out.println("🔧 初始化Keyspace和表结构...");

    // 创建keyspace
    String createKeyspace = "CREATE KEYSPACE IF NOT EXISTS " + KEYSPACE_NAME +
        " WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};";
    executeCQLCommand(createKeyspace, false);

    // 使用keyspace
    executeCQLCommand("USE " + KEYSPACE_NAME + ";", false);

    System.out.println("✅ 初始化完成");
  }

  /**
   * 基本CRUD演示 - Cassandra特色：时间序列和高吞吐量写入
   */
  private static void demonstrateBasicCRUD() {
    System.out.println("\n🚀 === Cassandra时间序列特性CRUD演示 ===");

    // 创建时间序列表 (体现Cassandra在时间序列数据方面的优势)
    System.out.println("\n📋 创建物联网传感器时间序列表:");
    String createSensorTable = "USE " + KEYSPACE_NAME + "; " +
        "CREATE TABLE IF NOT EXISTS sensor_readings (" +
        "device_id TEXT, " +           // 分区键 - 设备ID
        "sensor_type TEXT, " +          // 分区键 - 传感器类型
        "timestamp TIMESTAMP, " +       // 聚簇键 - 时间戳
        "reading_id TIMEUUID, " +       // 聚簇键 - 唯一标识
        "value DOUBLE, " +              // 传感器读数值
        "unit TEXT, " +                 // 单位
        "location TEXT, " +             // 位置信息
        "quality_score FLOAT, " +       // 数据质量评分
        "metadata MAP<TEXT, TEXT>, " +  // 额外元数据
        "PRIMARY KEY ((device_id, sensor_type), timestamp, reading_id)" +
        ") WITH CLUSTERING ORDER BY (timestamp DESC, reading_id DESC);";  // 按时间倒序排列

    System.out.println("命令: CREATE TABLE sensor_readings (专为时间序列优化)");
    executeCQLCommand(createSensorTable);

    // CREATE - 批量写入时间序列数据 (体现Cassandra高写入吞吐量)
    System.out.println("\n📝 CREATE - 批量写入时间序列数据:");

    System.out.println("1. 写入温度传感器数据 (近一小时的读数):");
    String[] temperatureData = {
        "INSERT INTO sensor_readings (device_id, sensor_type, timestamp, reading_id, value, unit, location, quality_score, metadata) VALUES ('DEVICE_001', 'temperature', '2024-01-20 14:00:00', now(), 23.5, 'celsius', '机房A-机架1', 0.95, {'calibrated': '2024-01-01', 'firmware': 'v2.1'});",
        "INSERT INTO sensor_readings (device_id, sensor_type, timestamp, reading_id, value, unit, location, quality_score, metadata) VALUES ('DEVICE_001', 'temperature', '2024-01-20 14:05:00', now(), 23.8, 'celsius', '机房A-机架1', 0.96, {'calibrated': '2024-01-01', 'firmware': 'v2.1'});",
        "INSERT INTO sensor_readings (device_id, sensor_type, timestamp, reading_id, value, unit, location, quality_score, metadata) VALUES ('DEVICE_001', 'temperature', '2024-01-20 14:10:00', now(), 24.1, 'celsius', '机房A-机架1', 0.94, {'calibrated': '2024-01-01', 'firmware': 'v2.1'});",
        "INSERT INTO sensor_readings (device_id, sensor_type, timestamp, reading_id, value, unit, location, quality_score, metadata) VALUES ('DEVICE_001', 'temperature', '2024-01-20 14:15:00', now(), 24.3, 'celsius', '机房A-机架1', 0.97, {'calibrated': '2024-01-01', 'firmware': 'v2.1'});"
    };

    for (String insert : temperatureData) {
      executeCQLCommand("USE " + KEYSPACE_NAME + "; " + insert);
    }

    System.out.println("2. 写入湿度传感器数据:");
    String[] humidityData = {
        "INSERT INTO sensor_readings (device_id, sensor_type, timestamp, reading_id, value, unit, location, quality_score, metadata) VALUES ('DEVICE_001', 'humidity', '2024-01-20 14:00:00', now(), 65.2, 'percent', '机房A-机架1', 0.92, {'calibrated': '2024-01-01', 'firmware': 'v2.1'});",
        "INSERT INTO sensor_readings (device_id, sensor_type, timestamp, reading_id, value, unit, location, quality_score, metadata) VALUES ('DEVICE_002', 'humidity', '2024-01-20 14:00:00', now(), 68.1, 'percent', '机房B-机架3', 0.89, {'calibrated': '2023-12-15', 'firmware': 'v2.0'});",
        "INSERT INTO sensor_readings (device_id, sensor_type, timestamp, reading_id, value, unit, location, quality_score, metadata) VALUES ('DEVICE_003', 'temperature', '2024-01-20 14:00:00', now(), 22.8, 'celsius', '机房C-机架5', 0.91, {'calibrated': '2024-01-10', 'firmware': 'v2.1'});"
    };

    for (String insert : humidityData) {
      executeCQLCommand("USE " + KEYSPACE_NAME + "; " + insert);
    }

    // READ - Cassandra特色时间序列查询
    System.out.println("\n🔍 READ - 时间序列特色查询:");

    System.out.println("1. 查询特定设备的温度读数 (按时间倒序):");
    executeCQLCommand("USE " + KEYSPACE_NAME + "; SELECT device_id, timestamp, value, unit, quality_score FROM sensor_readings WHERE device_id = 'DEVICE_001' AND sensor_type = 'temperature' LIMIT 10;");

    System.out.println("2. 查询指定时间范围内的所有温度读数:");
    executeCQLCommand("USE " + KEYSPACE_NAME + "; SELECT device_id, timestamp, value, location FROM sensor_readings WHERE device_id = 'DEVICE_001' AND sensor_type = 'temperature' AND timestamp >= '2024-01-20 14:05:00' AND timestamp <= '2024-01-20 14:15:00';");

    System.out.println("3. 查询最新的传感器读数 (利用时间排序优势):");
    executeCQLCommand("USE " + KEYSPACE_NAME + "; SELECT device_id, sensor_type, timestamp, value, unit FROM sensor_readings WHERE device_id = 'DEVICE_001' AND sensor_type = 'temperature' LIMIT 1;");

    // UPDATE - 时间序列数据通常是不可变的，但可以更新元数据
    System.out.println("\n✏️ UPDATE - 更新传感器元数据 (时间序列数据特点):");
    System.out.println("注意：时间序列数据通常是不可变的，但可以更新质量评分或元数据");

    // 创建设备信息表用于更新演示
    String createDeviceTable = "USE " + KEYSPACE_NAME + "; " +
        "CREATE TABLE IF NOT EXISTS device_info (" +
        "device_id TEXT PRIMARY KEY, " +
        "device_name TEXT, " +
        "location TEXT, " +
        "status TEXT, " +
        "last_maintenance TIMESTAMP, " +
        "firmware_version TEXT, " +
        "contact_info MAP<TEXT, TEXT>" +
        ");";
    executeCQLCommand(createDeviceTable);

    // 插入设备信息
    executeCQLCommand("USE " + KEYSPACE_NAME + "; INSERT INTO device_info (device_id, device_name, location, status, last_maintenance, firmware_version, contact_info) VALUES ('DEVICE_001', '温湿度传感器-001', '机房A-机架1', 'active', '2024-01-01 09:00:00', 'v2.1', {'admin': 'admin@company.com', 'tech': 'tech@company.com'});");

    System.out.println("更新设备状态和维护时间:");
    executeCQLCommand("USE " + KEYSPACE_NAME + "; UPDATE device_info SET status = 'maintenance', last_maintenance = toTimestamp(now()) WHERE device_id = 'DEVICE_001';");

    System.out.println("验证更新:");
    executeCQLCommand("USE " + KEYSPACE_NAME + "; SELECT * FROM device_info WHERE device_id = 'DEVICE_001';");

    // DELETE - 基于TTL的自动过期 (Cassandra特色)
    System.out.println("\n🗑️ DELETE - 基于TTL的数据生命周期管理:");
    System.out.println("演示：插入带有TTL的临时告警数据 (30秒后自动删除)");

    String createAlertTable = "USE " + KEYSPACE_NAME + "; " +
        "CREATE TABLE IF NOT EXISTS alerts (" +
        "device_id TEXT, " +
        "alert_time TIMESTAMP, " +
        "alert_type TEXT, " +
        "message TEXT, " +
        "severity TEXT, " +
        "PRIMARY KEY (device_id, alert_time)" +
        ") WITH CLUSTERING ORDER BY (alert_time DESC);";
    executeCQLCommand(createAlertTable);

    System.out.println("插入带TTL的告警 (30秒后自动过期):");
    executeCQLCommand("USE " + KEYSPACE_NAME + "; INSERT INTO alerts (device_id, alert_time, alert_type, message, severity) VALUES ('DEVICE_001', toTimestamp(now()), 'temperature_high', '温度超过阈值: 24.3°C > 24.0°C', 'warning') USING TTL 30;");

    System.out.println("查看告警记录:");
    executeCQLCommand("USE " + KEYSPACE_NAME + "; SELECT * FROM alerts WHERE device_id = 'DEVICE_001';");

    System.out.println("注意：30秒后该告警记录将自动删除 (Cassandra TTL特性)");

    System.out.println("\n✅ Cassandra时间序列特性CRUD演示完成！");
  }

  /**
   * Keyspace操作演示
   */
  private static void demonstrateKeyspaceOperations() {
    System.out.println("\n🏠 === Keyspace操作演示 ===");

    // 创建新的keyspace
    System.out.println("\n➕ 创建新Keyspace:");
    String createKs = "CREATE KEYSPACE IF NOT EXISTS demo_ks " +
        "WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};";
    System.out.println("命令: CREATE KEYSPACE demo_ks");
    executeCQLCommand(createKs);

    // 显示所有keyspaces
    System.out.println("\n🔍 查看所有Keyspaces:");
    System.out.println("命令: DESCRIBE KEYSPACES");
    executeCQLCommand("DESCRIBE KEYSPACES;");

    // 查看keyspace详情
    System.out.println("\n📊 查看Keyspace详情:");
    System.out.println("命令: DESCRIBE KEYSPACE " + KEYSPACE_NAME);
    executeCQLCommand("DESCRIBE KEYSPACE " + KEYSPACE_NAME + ";");

    System.out.println("✅ Keyspace操作演示完成！");
  }

  /**
   * 表操作演示
   */
  private static void demonstrateTableOperations() {
    System.out.println("\n📋 === 表操作演示 ===");

    // 创建产品表
    System.out.println("\n➕ 创建产品表:");
    String createProductTable = "USE " + KEYSPACE_NAME + "; " +
        "CREATE TABLE IF NOT EXISTS products (" +
        "category TEXT, " +
        "id UUID, " +
        "name TEXT, " +
        "price DECIMAL, " +
        "description TEXT, " +
        "PRIMARY KEY (category, id));";
    System.out.println("命令: CREATE TABLE products");
    executeCQLCommand(createProductTable);

    // 插入产品数据
    System.out.println("\n📦 插入产品数据:");
    String insertProduct1 = "USE " + KEYSPACE_NAME + "; " +
        "INSERT INTO products (category, id, name, price, description) " +
        "VALUES ('电子产品', uuid(), '笔记本电脑', 5000.00, '高性能笔记本电脑');";
    executeCQLCommand(insertProduct1);

    String insertProduct2 = "USE " + KEYSPACE_NAME + "; " +
        "INSERT INTO products (category, id, name, price, description) " +
        "VALUES ('电子产品', uuid(), '智能手机', 3000.00, '最新款智能手机');";
    executeCQLCommand(insertProduct2);

    String insertProduct3 = "USE " + KEYSPACE_NAME + "; " +
        "INSERT INTO products (category, id, name, price, description) " +
        "VALUES ('家具', uuid(), '办公椅', 800.00, '舒适办公椅');";
    executeCQLCommand(insertProduct3);

    // 查询产品
    System.out.println("\n🔍 查询产品:");
    System.out.println("命令: SELECT * FROM products");
    executeCQLCommand("USE " + KEYSPACE_NAME + "; SELECT * FROM products;");

    // 查询特定分类
    System.out.println("\n🎯 按分类查询:");
    System.out.println("命令: SELECT * FROM products WHERE category = '电子产品'");
    executeCQLCommand("USE " + KEYSPACE_NAME + "; SELECT * FROM products WHERE category = '电子产品';");

    // 查看表结构
    System.out.println("\n📋 查看表结构:");
    System.out.println("命令: DESCRIBE TABLE products");
    executeCQLCommand("USE " + KEYSPACE_NAME + "; DESCRIBE TABLE products;");

    System.out.println("✅ 表操作演示完成！");
  }

  /**
   * 查询操作演示
   */
  private static void demonstrateQueryOperations() {
    System.out.println("\n🔍 === 查询操作演示 ===");

    // 创建时间序列表演示
    System.out.println("\n📊 创建时间序列表:");
    String createTimeSeriesTable = "USE " + KEYSPACE_NAME + "; " +
        "CREATE TABLE IF NOT EXISTS sensor_data (" +
        "sensor_id TEXT, " +
        "timestamp TIMESTAMP, " +
        "temperature FLOAT, " +
        "humidity FLOAT, " +
        "PRIMARY KEY (sensor_id, timestamp)) " +
        "WITH CLUSTERING ORDER BY (timestamp DESC);";
    executeCQLCommand(createTimeSeriesTable);

    // 插入时间序列数据
    System.out.println("\n📈 插入传感器数据:");
    String[] sensorInserts = {
        "INSERT INTO sensor_data (sensor_id, timestamp, temperature, humidity) VALUES ('sensor_001', toTimestamp(now()), 23.5, 65.2);",
        "INSERT INTO sensor_data (sensor_id, timestamp, temperature, humidity) VALUES ('sensor_001', '2024-01-01 10:00:00', 24.1, 63.8);",
        "INSERT INTO sensor_data (sensor_id, timestamp, temperature, humidity) VALUES ('sensor_001', '2024-01-01 11:00:00', 24.8, 62.5);",
        "INSERT INTO sensor_data (sensor_id, timestamp, temperature, humidity) VALUES ('sensor_002', toTimestamp(now()), 22.3, 68.1);"
    };

    for (String insert : sensorInserts) {
      executeCQLCommand("USE " + KEYSPACE_NAME + "; " + insert);
    }

    // 查询操作
    System.out.println("\n🔍 各种查询操作:");

    System.out.println("1. 查询所有传感器数据:");
    executeCQLCommand("USE " + KEYSPACE_NAME + "; SELECT * FROM sensor_data;");

    System.out.println("\n2. 查询特定传感器数据:");
    executeCQLCommand("USE " + KEYSPACE_NAME + "; SELECT * FROM sensor_data WHERE sensor_id = 'sensor_001';");

    System.out.println("\n3. 限制结果数量:");
    executeCQLCommand("USE " + KEYSPACE_NAME + "; SELECT * FROM sensor_data LIMIT 3;");

    // 聚合查询
    System.out.println("\n📈 聚合查询:");
    System.out.println("命令: SELECT COUNT(*) FROM sensor_data");
    executeCQLCommand("USE " + KEYSPACE_NAME + "; SELECT COUNT(*) FROM sensor_data;");

    System.out.println("✅ 查询操作演示完成！");
  }

  /**
   * 执行自定义CQL命令
   */
  private static void executeCustomCommand() {
    System.out.println("\n✍️ 自定义CQL命令执行模式");
    System.out.println("💡 提示: 输入 'help' 查看常用命令，输入 'quit' 退出");
    System.out.println("⚠️  注意: 命令会自动添加 USE " + KEYSPACE_NAME + "; 前缀");

    while (true) {
      System.out.print("\nCQL> ");
      String command = scanner.nextLine().trim();

      if ("quit".equalsIgnoreCase(command)) {
        System.out.println("👋 退出自定义命令模式");
        break;
      }

      if ("help".equalsIgnoreCase(command)) {
        showCassandraHelp();
        continue;
      }

      if (!command.isEmpty()) {
        System.out.println("🔍 执行: " + command);
        // 自动添加USE keyspace前缀
        String fullCommand = "USE " + KEYSPACE_NAME + "; " + command;
        executeCQLCommand(fullCommand);
      }
    }
  }

  /**
   * 显示Cassandra帮助信息
   */
  private static void showCassandraHelp() {
    System.out.println("\n💡 常用CQL命令:");
    System.out.println("创建表: CREATE TABLE table_name (id UUID PRIMARY KEY, name TEXT);");
    System.out.println("插入数据: INSERT INTO table_name (id, name) VALUES (uuid(), 'value');");
    System.out.println("查询数据: SELECT * FROM table_name;");
    System.out.println("更新数据: UPDATE table_name SET field = 'new_value' WHERE id = uuid();");
    System.out.println("删除数据: DELETE FROM table_name WHERE id = uuid();");
    System.out.println("查看表: DESCRIBE TABLES;");
    System.out.println("查看表结构: DESCRIBE TABLE table_name;");
  }

  /**
   * 显示Cassandra信息
   */
  private static void showCassandraInfo() {
    System.out.println("\n📊 === Cassandra信息 ===");

    System.out.println("🏠 所有Keyspaces:");
    System.out.println("命令: DESCRIBE KEYSPACES");
    executeCQLCommand("DESCRIBE KEYSPACES;");

    System.out.println("\n📋 当前Keyspace的表:");
    System.out.println("命令: USE " + KEYSPACE_NAME + "; DESCRIBE TABLES");
    executeCQLCommand("USE " + KEYSPACE_NAME + "; DESCRIBE TABLES;");

    System.out.println("\n🔧 集群信息:");
    System.out.println("命令: SELECT * FROM system.local");
    executeCQLCommand("SELECT cluster_name, data_center, rack FROM system.local;");

    System.out.println("\n📊 节点信息:");
    System.out.println("命令: SELECT * FROM system.peers");
    executeCQLCommand("SELECT peer, data_center, rack FROM system.peers;");
  }

  /**
   * 清空所有数据
   */
  private static void clearAllData() {
    System.out.println("\n🗑️ === 清空数据 ===");
    System.out.print("⚠️ 确定要删除keyspace " + KEYSPACE_NAME + " 吗？(输入 'yes' 确认): ");
    String confirm = scanner.nextLine().trim();

    if ("yes".equalsIgnoreCase(confirm)) {
      System.out.println("命令: DROP KEYSPACE " + KEYSPACE_NAME);
      executeCQLCommand("DROP KEYSPACE IF EXISTS " + KEYSPACE_NAME + ";");
      System.out.println("✅ Keyspace已删除");

      // 重新创建keyspace供后续使用
      System.out.println("🔧 重新创建Keyspace...");
      initializeKeyspaceAndTables();
    } else {
      System.out.println("❌ 操作已取消");
    }
  }
}