package com.example.springDemo.controller;

import com.example.springDemo.model.Student;
import com.example.springDemo.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

  private final StudentService postgresStudentService;
  private final StudentService mysqlStudentService;

  @Autowired
  public StudentController(
      @Qualifier("postgresStudentService") StudentService postgresStudentService,
      @Qualifier("mysqlStudentService") StudentService mysqlStudentService) {
    this.postgresStudentService = postgresStudentService;
    this.mysqlStudentService = mysqlStudentService;
  }

  @GetMapping("/postgres")
  public ResponseEntity<List<Student>> getAllPostgresStudents() {
    return ResponseEntity.ok(postgresStudentService.getAllStudents());
  }

  @GetMapping("/mysql")
  public ResponseEntity<List<Student>> getAllMysqlStudents() {
    return ResponseEntity.ok(mysqlStudentService.getAllStudents());
  }

  @GetMapping("/postgres/{id}")
  public ResponseEntity<Student> getPostgresStudentById(@PathVariable Long id) {
    return postgresStudentService.getStudentById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/mysql/{id}")
  public ResponseEntity<Student> getMysqlStudentById(@PathVariable Long id) {
    return mysqlStudentService.getStudentById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  /**
   * POST: http://localhost:8080/api/students/mysql
   * test data:
   * {
   *   "name": "alice",
   *   "age": 22,
   *   "ssn": "123-45-6789",
   *   "birth": "2000-01-01",
   *   "creditCardNumber": "4111-1111-1111-1111"
   * }
   *
   */
  @PostMapping("/mysql")
  public ResponseEntity<Student> createMysqlStudent(@RequestBody Student student) {
    return new ResponseEntity<>(mysqlStudentService.saveStudent(student), HttpStatus.CREATED);
  }

  @PostMapping("/postgres")
  public ResponseEntity<Student> createPostgresStudent(@RequestBody Student student) {
    return new ResponseEntity<>(postgresStudentService.saveStudent(student), HttpStatus.CREATED);
  }

  @PutMapping("/mysql/{id}")
  public ResponseEntity<Student> updateMysqlStudent(@PathVariable Long id, @RequestBody Student student) {
    return mysqlStudentService.getStudentById(id)
        .map(existingStudent -> {
          student.setId(id);
          return ResponseEntity.ok(mysqlStudentService.saveStudent(student));
        })
        .orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/postgres/{id}")
  public ResponseEntity<Student> updatePostgresStudent(@PathVariable Long id, @RequestBody Student student) {
    return postgresStudentService.getStudentById(id)
        .map(existingStudent -> {
          student.setId(id);
          return ResponseEntity.ok(postgresStudentService.saveStudent(student));
        })
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/mysql/{id}")
  public ResponseEntity<Void> deleteMysqlStudent(@PathVariable Long id) {
    return mysqlStudentService.getStudentById(id)
        .map(student -> {
          mysqlStudentService.deleteStudent(id);
          return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        })
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/postgres/{id}")
  public ResponseEntity<Void> deletePostgresStudent(@PathVariable Long id) {
    return postgresStudentService.getStudentById(id)
        .map(student -> {
          postgresStudentService.deleteStudent(id);
          return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        })
        .orElse(ResponseEntity.notFound().build());
  }

  // 示例：根据数据源参数动态选择服务
  @GetMapping
  public ResponseEntity<List<Student>> getAllStudents(
      @RequestParam(name = "source", defaultValue = "postgres") String source) {
    if ("mysql".equalsIgnoreCase(source)) {
      return ResponseEntity.ok(mysqlStudentService.getAllStudents());
    } else {
      return ResponseEntity.ok(postgresStudentService.getAllStudents());
    }
  }
}