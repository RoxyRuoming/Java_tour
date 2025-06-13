package com.example.springDemo.repository.mysql;

import com.example.springDemo.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MysqlStudentRepository extends JpaRepository<Student, Long> {
  List<Student> findByName(String name);
  List<Student> findByAgeGreaterThan(Integer age);
}