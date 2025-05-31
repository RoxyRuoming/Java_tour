//package org.example.nonsqlExamples.mongodb;
//
//import java.io.*;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Scanner;
//
//public class MongoDBRunner {
//
//  private static final String CONTAINER_NAME = "mongodb";
//  private static final String DATABASE_NAME = "blogdb";
//  private static final String MONGO_USERNAME = "admin";
//  private static final String MONGO_PASSWORD = "admin123";
//
//  private static final Scanner scanner = new Scanner(System.in);
//
//  public static void main(String[] args) {
//    System.out.println("=== MongoDB Blog Post Operations ===");
//    System.out.println("MongoDB container: " + CONTAINER_NAME);
//    System.out.println();
//
//    if (!testConnection()) {
//      System.err.println("❌ Could not connect to MongoDB container");
//      return;
//    }
//
//    System.out.println("✅ MongoDB connection successful!");
//
//    while (true) {
//      showMenu();
//      int choice = getChoice();
//
//      switch (choice) {
//        case 1:
//          createBlogPost();
//          break;
//        case 2:
//          listBlogPosts();
//          break;
//        case 3:
//          searchBlogPosts();
//          break;
//        case 4:
//          updateBlogPost();
//          break;
//        case 5:
//          deleteBlogPost();
//          break;
//        case 6:
//          showBlogStats();
//          break;
//        case 0:
//          System.out.println("👋 Goodbye!");
//          scanner.close();
//          return;
//        default:
//          System.out.println("❌ Invalid choice");
//      }
//
//      System.out.println("\nPress Enter to continue...");
//      scanner.nextLine();
//    }
//  }
//
//  private static void showMenu() {
//    System.out.println("\n" + "=".repeat(50));
//    System.out.println("Blog Post Operations:");
//    System.out.println("1. 📝 Create new blog post");
//    System.out.println("2. 🔍 List all blog posts");
//    System.out.println("3. 🔎 Search blog posts");
//    System.out.println("4. ✏️ Update blog post");
//    System.out.println("5. 🗑️ Delete blog post");
//    System.out.println("6. 📊 Show blog statistics");
//    System.out.println("0. 👋 Exit");
//    System.out.println("=".repeat(50));
//    System.out.print("Enter choice (0-6): ");
//  }
//
//  private static int getChoice() {
//    try {
//      String input = scanner.nextLine().trim();
//      return Integer.parseInt(input);
//    } catch (NumberFormatException e) {
//      return -1;
//    }
//  }
//
//  private static boolean testConnection() {
//    System.out.println("🔗 Testing MongoDB connection...");
//    return executeMongoCommand("db.runCommand({ping: 1})");
//  }
//  // process builder - Java中启动外部程序的工具
//  private static boolean executeMongoCommand(String command) { // 通过Docker执行MongoDB Shell (mongosh)命令
//    try {
//      List<String> cmd = new ArrayList<>();
//      cmd.add("docker");
//      cmd.add("exec");
//      cmd.add(CONTAINER_NAME);
//      cmd.add("mongosh");
//      cmd.add("--quiet");
//      cmd.add("-u");
//      cmd.add(MONGO_USERNAME);
//      cmd.add("-p");
//      cmd.add(MONGO_PASSWORD);
//      cmd.add("--authenticationDatabase");
//      cmd.add("admin");
//      cmd.add(DATABASE_NAME);
//      cmd.add("--eval");
//      cmd.add(command);
//
//      ProcessBuilder pb = new ProcessBuilder(cmd);
//      pb.redirectErrorStream(true);
//      Process process = pb.start();
//
//      boolean hasOutput = false;
//      try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
//        String line;
//        while ((line = reader.readLine()) != null) {
//          System.out.println(line);
//          hasOutput = true;
//        }
//      }
//
//      int exitCode = process.waitFor();
//
//      if (!hasOutput && exitCode == 0) {
//        System.out.println("(Command executed successfully)");
//      }
//
//      return exitCode == 0;
//
//    } catch (IOException | InterruptedException e) {
//      System.err.println("❌ Failed to execute MongoDB command: " + e.getMessage());
//      return false;
//    }
//  }
//
//  private static void createBlogPost() {
//    System.out.println("\n📝 Creating new blog post");
//
//    System.out.print("Enter title: ");
//    String title = scanner.nextLine();
//
//    System.out.print("Enter content: ");
//    String content = scanner.nextLine();
//
//    System.out.print("Enter author name: ");
//    String author = scanner.nextLine();
//
//    System.out.print("Enter tags (comma separated): ");
//    String[] tags = scanner.nextLine().split(",");
//
//    String insertCommand = String.format(
//        "db.posts.insertOne({" +
//            "title: '%s', " +
//            "content: '%s', " +
//            "author: {name: '%s'}, " +
//            "tags: %s, " +
//            "createdAt: new Date(), " +
//            "views: 0, " +
//            "likes: 0" +
//            "})",
//        title, content, author, java.util.Arrays.toString(tags));
//
//    executeMongoCommand(insertCommand);
//  }
//
//  private static void listBlogPosts() {
//    System.out.println("\n📄 Listing all blog posts:");
//    executeMongoCommand("db.posts.find().pretty()");
//  }
//
//  private static void searchBlogPosts() {
//    System.out.println("\n🔎 Search options:");
//    System.out.println("1. Search by title");
//    System.out.println("2. Search by tag");
//    System.out.println("3. Search by author");
//    System.out.print("Enter search option: ");
//
//    int option = getChoice();
//    String searchCommand = "";
//
//    switch (option) {
//      case 1:
//        System.out.print("Enter title to search: ");
//        String title = scanner.nextLine();
//        searchCommand = String.format("db.posts.find({title: /%s/i}).pretty()", title);
//        break;
//      case 2:
//        System.out.print("Enter tag to search: ");
//        String tag = scanner.nextLine();
//        searchCommand = String.format("db.posts.find({tags: '%s'}).pretty()", tag);
//        break;
//      case 3:
//        System.out.print("Enter author to search: ");
//        String author = scanner.nextLine();
//        searchCommand = String.format("db.posts.find({'author.name': /%s/i}).pretty()", author);
//        break;
//      default:
//        System.out.println("Invalid option");
//        return;
//    }
//
//    executeMongoCommand(searchCommand);
//  }
//
//  private static void updateBlogPost() {
//    System.out.println("\n✏️ Update blog post");
//    System.out.print("Enter post title to update: ");
//    String title = scanner.nextLine();
//
//    System.out.println("What to update?");
//    System.out.println("1. Update content");
//    System.out.println("2. Add tags");
//    System.out.println("3. Increment views");
//    System.out.println("4. Increment likes");
//    System.out.print("Enter option: ");
//
//    int option = getChoice();
//    String updateCommand = "";
//
//    switch (option) {
//      case 1:
//        System.out.print("Enter new content: ");
//        String content = scanner.nextLine();
//        updateCommand = String.format("db.posts.updateOne({title: '%s'}, {$set: {content: '%s'}})", title, content);
//        break;
//      case 2:
//        System.out.print("Enter tags to add (comma separated): ");
//        String[] tags = scanner.nextLine().split(",");
//        updateCommand = String.format("db.posts.updateOne({title: '%s'}, {$push: {tags: {$each: %s}}})",
//            title, java.util.Arrays.toString(tags));
//        break;
//      case 3:
//        updateCommand = String.format("db.posts.updateOne({title: '%s'}, {$inc: {views: 1}})", title);
//        break;
//      case 4:
//        updateCommand = String.format("db.posts.updateOne({title: '%s'}, {$inc: {likes: 1}})", title);
//        break;
//      default:
//        System.out.println("Invalid option");
//        return;
//    }
//
//    executeMongoCommand(updateCommand);
//  }
//
//  private static void deleteBlogPost() {
//    System.out.println("\n🗑️ Delete blog post");
//    System.out.print("Enter post title to delete: ");
//    String title = scanner.nextLine();
//
//    String deleteCommand = String.format("db.posts.deleteOne({title: '%s'})", title);
//    executeMongoCommand(deleteCommand);
//  }
//
//  private static void showBlogStats() {
//    System.out.println("\n📊 Blog Statistics:");
//
//    System.out.println("Total posts:");
//    executeMongoCommand("db.posts.countDocuments()");
//
//    System.out.println("\nMost viewed posts:");
//    executeMongoCommand("db.posts.find().sort({views: -1}).limit(5).pretty()");
//
//    System.out.println("\nMost liked posts:");
//    executeMongoCommand("db.posts.find().sort({likes: -1}).limit(5).pretty()");
//
//    System.out.println("\nPopular tags:");
//    executeMongoCommand("db.posts.aggregate([{$unwind: '$tags'}, {$group: {_id: '$tags', count: {$sum: 1}}}, {$sort: {count: -1}}])");
//  }
//}