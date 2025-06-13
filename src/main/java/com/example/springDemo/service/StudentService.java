package com.example.springDemo.service;

import com.example.springDemo.model.Student;
import java.util.List;
import java.util.Optional;

public interface StudentService {
  List<Student> getAllStudents();
  Optional<Student> getStudentById(Long id);
  Student saveStudent(Student student);
  void deleteStudent(Long id);
  List<Student> findStudentsByName(String name);
  List<Student> findStudentsOlderThan(Integer age);
}