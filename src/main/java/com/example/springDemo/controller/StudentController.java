package com.example.springDemo.controller;

import com.example.springDemo.model.Student;
import com.example.springDemo.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // return data instead of view
@RequestMapping("/api/students") // basic url mapping
public class StudentController {

  private final StudentService studentService; // 通过构造器注入StudentService，遵循依赖倒置原则

  @Autowired
  public StudentController(StudentService studentService) {
    this.studentService = studentService;
  }

  @GetMapping
  public ResponseEntity<List<Student>> getAllStudents() {
    return ResponseEntity.ok(studentService.getAllStudents()); // return 200 OK
  }

  @GetMapping("/{id}")
  public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
    ResponseEntity<Student> studentResponseEntity = studentService.getStudentById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());// orElse - java8 optional

    return studentResponseEntity;
    // .build - Builder Pattern
  }

  @PostMapping
  public ResponseEntity<Student> createStudent(@RequestBody Student student) {
    return new ResponseEntity<>(studentService.saveStudent(student), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Student> updateStudent(@PathVariable Long id, @RequestBody Student student) {
    return studentService.getStudentById(id)
        .map(existingStudent -> {
          student.setId(id);
          return ResponseEntity.ok(studentService.saveStudent(student));
        })
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
    return studentService.getStudentById(id)
        .map(student -> {
          studentService.deleteStudent(id);
          return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        })
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/search")
  public ResponseEntity<List<Student>> searchStudents(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) Integer minAge) {

    if (name != null) {
      return ResponseEntity.ok(studentService.findStudentsByName(name));
    } else if (minAge != null) {
      return ResponseEntity.ok(studentService.findStudentsOlderThan(minAge));
    }

    return ResponseEntity.ok(studentService.getAllStudents());
  }

  @GetMapping("/test-exception")
  public ResponseEntity<Student> testException() {
    throw new RuntimeException("This is a test exception for AOP logging");
  }
}