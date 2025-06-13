package com.example.springDemo.service.impl;

import com.example.springDemo.model.Student;
import com.example.springDemo.repository.postgres.PostgresStudentRepository;
import com.example.springDemo.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("postgresStudentService")
@Primary  // 将此实现设为主要实现，如果没有指定具体实现，则使用此实现
public class StudentServiceImplPostgres implements StudentService {

  private final PostgresStudentRepository studentRepository;

  @Autowired
  public StudentServiceImplPostgres(PostgresStudentRepository studentRepository) {
    this.studentRepository = studentRepository;
  }

  @Override
  public List<Student> getAllStudents() {
    return studentRepository.findAll();
  }

  @Override
  public Optional<Student> getStudentById(Long id) {
    Optional<Student> byId = studentRepository.findById(id);
    Student std = byId.orElse(null);
    if (std != null) {
      return Optional.of(std);
    } else {
      return Optional.empty();
    }
  }

  @Override
  public Student saveStudent(Student student) {
    return studentRepository.save(student);
  }

  @Override
  public void deleteStudent(Long id) {
    studentRepository.deleteById(id);
  }

  @Override
  public List<Student> findStudentsByName(String name) {
    return studentRepository.findByName(name);
  }

  @Override
  public List<Student> findStudentsOlderThan(Integer age) {
    return studentRepository.findByAgeGreaterThan(age);
  }
}