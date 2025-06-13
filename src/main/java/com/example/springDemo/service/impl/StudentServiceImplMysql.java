package com.example.springDemo.service.impl;

import com.example.springDemo.model.Student;
import com.example.springDemo.repository.mysql.MysqlStudentRepository;
import com.example.springDemo.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("mysqlStudentService")
public class StudentServiceImplMysql implements StudentService {

  private final MysqlStudentRepository studentRepository;

  @Autowired
  public StudentServiceImplMysql(MysqlStudentRepository studentRepository) {
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