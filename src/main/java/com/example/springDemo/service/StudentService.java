package com.example.springDemo.service;

/**
 * 1. write another SpringBoot application
 * that expose 4 endpoints(CRUD operations)
 * can send a request to SpringBoot application(existing one)
 * eg.
 * DROP TABLE IF EXISTS students;
 *
 * CREATE TABLE students (
 *                           id SERIAL PRIMARY KEY,
 *                           name VARCHAR(100) NOT NULL,
 *                           age INTEGER
 *
 * --                       ssn
 * --                       birth
 * --                       credit card
 * -- create a DTO (id, name, age - useful info) to send a http request(payload) to the service1
 * );
 *
 * 2. using Miro
 * Monolithic vs micro-service (pain point: communnication)
 *
 *  api gateway, service discovery, log monitoring,
 *  business modules (springboot application),
 *  load balancer, circuit breaker, configuration server
 *
 *  user ->
 *  browse shopping list    -> browsing service(springboot application)
 *  pick items              -> shopping service
 *  make order              -> order service
 *  make payment            -> payment service
 *  email notification sent -> email service
 *
 *
 *
 *
 *
 */


import com.example.springDemo.model.Student;
import com.example.springDemo.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

  private final StudentRepository studentRepository;

  @Autowired
  public StudentService(StudentRepository studentRepository) {
    this.studentRepository = studentRepository;
  }

  public List<Student> getAllStudents() {
    return studentRepository.findAll();
  }

  public Optional<Student> getStudentById(Long id) {
    Optional<Student> byId = studentRepository.findById(id);
    Student std = byId.orElse(null);  // 从Optional中获取Student对象，如果不存在则为null
    if (std != null) {
      // 如果需要对std进行任何处理，可以在这里添加代码
      byId = Optional.of(std);  // 将std重新包装为Optional
    } else {
      return Optional.empty();  // 如果std为null，返回空Optional
    }
    return byId;
  }

  public Student saveStudent(Student student) {
    return studentRepository.save(student);
  }

  public void deleteStudent(Long id) {
    studentRepository.deleteById(id);
  }

  public List<Student> findStudentsByName(String name) {
    return studentRepository.findByName(name);
  }

  public List<Student> findStudentsOlderThan(Integer age) {
    return studentRepository.findByAgeGreaterThan(age);
  }
}