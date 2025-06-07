package org.example.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.example.model.Student;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/http-students")
public class HttpServletStudent extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    Session session = null;
    PrintWriter out = null;
    try {
      session = HibernateUtil.getSessionFactory().openSession();

      String idParam = req.getParameter("id");
      if (idParam != null && !idParam.trim().isEmpty()) {
        // Get single student by ID
        try {
          int id = Integer.parseInt(idParam);
          Student student = session.get(Student.class, id);
          resp.setContentType("application/json");
          out = resp.getWriter();
          if (student != null) {
            out.printf("{\"id\":%d,\"name\":\"%s\",\"age\":%d}",
                student.getId(), student.getName(), student.getAge());
          } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print("{\"error\":\"Student not found\"}");
          }
        } catch (NumberFormatException e) {
          resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
          resp.getWriter().print("{\"error\":\"Invalid ID format\"}");
          return;
        }
      } else {
        // Get all students
        List<Student> students = session.createQuery("from Student", Student.class).list();
        resp.setContentType("application/json");
        out = resp.getWriter();
        out.print("[");
        for (int i = 0; i < students.size(); i++) {
          Student s = students.get(i);
          out.printf("{\"id\":%d,\"name\":\"%s\",\"age\":%d}",
              s.getId(), s.getName(), s.getAge());
          if (i < students.size() - 1) out.print(",");
        }
        out.print("]");
      }
    } catch (Exception e) {
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      if (out != null) {
        out.print("{\"error\":\"Failed to retrieve students\"}");
      }
      e.printStackTrace();
    } finally {
      if (session != null && session.isOpen()) {
        session.close();
      }
      if (out != null) {
        out.close();
      }
    }
  }

  @Override
  protected void doPut(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String idStr = req.getParameter("id");
    String name = req.getParameter("name");
    String ageStr = req.getParameter("age");

    // Input validation - only ID is required
    if (idStr == null || idStr.trim().isEmpty()) {
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      resp.getWriter().print("{\"error\":\"ID is required\"}");
      return;
    }

    // At least one field (name or age) should be provided
    if ((name == null || name.trim().isEmpty()) &&
        (ageStr == null || ageStr.trim().isEmpty())) {
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      resp.getWriter().print("{\"error\":\"At least one field (name or age) must be provided\"}");
      return;
    }

    int id, age = 0;
    try {
      id = Integer.parseInt(idStr);
      if (ageStr != null && !ageStr.trim().isEmpty()) {
        age = Integer.parseInt(ageStr);
        if (age <= 0) {
          throw new NumberFormatException("Age must be positive");
        }
      }
    } catch (NumberFormatException e) {
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      resp.getWriter().print("{\"error\":\"Invalid ID or age format\"}");
      return;
    }

    Session session = null;
    Transaction tx = null;
    try {
      session = HibernateUtil.getSessionFactory().openSession();
      tx = session.beginTransaction();

      Student student = session.get(Student.class, id);
      if (student != null) {
        // Only update fields that were provided
        if (name != null && !name.trim().isEmpty()) {
          student.setName(name);
        }
        if (ageStr != null && !ageStr.trim().isEmpty()) {
          student.setAge(age);
        }

        session.merge(student);
        tx.commit();
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().print("{\"message\":\"Student updated successfully\"}");
      } else {
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        resp.getWriter().print("{\"error\":\"Student not found\"}");
      }
    } catch (Exception e) {
      if (tx != null && tx.isActive()) {
        tx.rollback();
      }
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      resp.getWriter().print("{\"error\":\"Failed to update student\"}");
      e.printStackTrace();
    } finally {
      if (session != null && session.isOpen()) {
        session.close();
      }
    }
  }


  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String name = req.getParameter("name");
    String ageStr = req.getParameter("age");

    // Input validation
    if (name == null || name.trim().isEmpty() || ageStr == null || ageStr.trim().isEmpty()) {
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      resp.getWriter().print("{\"error\":\"Name and age are required\"}");
      return;
    }

    int age;
    try {
      age = Integer.parseInt(ageStr);
      if (age <= 0) {
        throw new NumberFormatException("Age must be positive");
      }
    } catch (NumberFormatException e) {
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      resp.getWriter().print("{\"error\":\"Invalid age format\"}");
      return;
    }

    Session session = null;
    Transaction tx = null;
    try {
      session = HibernateUtil.getSessionFactory().openSession();
      tx = session.beginTransaction();

      Student student = new Student();
      student.setName(name);
      student.setAge(age);

      session.persist(student);
      tx.commit();

      resp.setStatus(HttpServletResponse.SC_CREATED);
      resp.getWriter().print("{\"id\":" + student.getId() + "}");
    } catch (Exception e) {
      if (tx != null && tx.isActive()) {
        tx.rollback();
      }
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      resp.getWriter().print("{\"error\":\"Failed to create student\"}");
      e.printStackTrace();
    } finally {
      if (session != null && session.isOpen()) {
        session.close();
      }
    }
  }


  @Override
  protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String idStr = req.getParameter("id");

    // Input validation
    if (idStr == null || idStr.trim().isEmpty()) {
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      resp.getWriter().print("{\"error\":\"ID is required\"}");
      return;
    }

    int id;
    try {
      id = Integer.parseInt(idStr);
    } catch (NumberFormatException e) {
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      resp.getWriter().print("{\"error\":\"Invalid ID format\"}");
      return;
    }

    Session session = null;
    Transaction tx = null;
    try {
      session = HibernateUtil.getSessionFactory().openSession();
      tx = session.beginTransaction();

      Student student = session.get(Student.class, id);
      if (student != null) {
        session.remove(student);
        tx.commit();
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().print("{\"message\":\"Student deleted successfully\"}");
      } else {
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        resp.getWriter().print("{\"error\":\"Student not found\"}");
      }
    } catch (Exception e) {
      if (tx != null && tx.isActive()) {
        tx.rollback();
      }
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      resp.getWriter().print("{\"error\":\"Failed to delete student\"}");
      e.printStackTrace();
    } finally {
      if (session != null && session.isOpen()) {
        session.close();
      }
    }
  }
}