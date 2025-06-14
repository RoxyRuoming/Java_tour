package com.example.springDemo.repository.postgres;

import com.example.springDemo.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostgresStudentRepository extends JpaRepository<Student, Long> {
  List<Student> findByName(String name);
  List<Student> findByAgeGreaterThan(Integer age);
}