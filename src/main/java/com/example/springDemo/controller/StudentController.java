package com.example.springDemo.controller;

import com.example.springDemo.dto.StudentDTO;
import com.example.springDemo.model.Student;
import com.example.springDemo.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/students")
public class StudentController {

  private final StudentService studentService;

  @Autowired
  public StudentController(StudentService studentService) {
    this.studentService = studentService;
  }

  // 转换单个 Student 到 StudentDTO
  private StudentDTO convertToDTO(Student student) {
    return new StudentDTO(student.getId(), student.getName(), student.getAge());
  }

  // 转换 Student 列表到 StudentDTO 列表
  private List<StudentDTO> convertToDTOList(List<Student> students) {
    return students.stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  @GetMapping
  public ResponseEntity<List<StudentDTO>> getAllStudents() {
    List<Student> students = studentService.getAllStudents();
    return ResponseEntity.ok(convertToDTOList(students));
  }

  @GetMapping("/{id}")
  public ResponseEntity<StudentDTO> getStudentById(@PathVariable Long id) {
    return studentService.getStudentById(id)
        .map(student -> ResponseEntity.ok(convertToDTO(student)))
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<StudentDTO> createStudent(@RequestBody Student student) {
    Student savedStudent = studentService.saveStudent(student);
    return new ResponseEntity<>(convertToDTO(savedStudent), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<StudentDTO> updateStudent(@PathVariable Long id, @RequestBody Student student) {
    return studentService.getStudentById(id)
        .map(existingStudent -> {
          // 保留敏感信息
          if (student.getSsn() == null) {
            student.setSsn(existingStudent.getSsn());
          }
          if (student.getBirth() == null) {
            student.setBirth(existingStudent.getBirth());
          }
          if (student.getCreditCardNumber() == null) {
            student.setCreditCardNumber(existingStudent.getCreditCardNumber());
          }

          student.setId(id);
          Student updatedStudent = studentService.saveStudent(student);
          return ResponseEntity.ok(convertToDTO(updatedStudent));
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
  public ResponseEntity<List<StudentDTO>> searchStudents(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) Integer minAge) {

    List<Student> students;
    if (name != null) {
      students = studentService.findStudentsByName(name);
    } else if (minAge != null) {
      students = studentService.findStudentsOlderThan(minAge);
    } else {
      students = studentService.getAllStudents();
    }

    return ResponseEntity.ok(convertToDTOList(students));
  }

  @GetMapping("/test-exception")
  public ResponseEntity<StudentDTO> testException() {
    throw new RuntimeException("This is a test exception for AOP logging");
  }
}