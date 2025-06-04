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

// 使用HttpServlet实现的版本
@WebServlet("/http-students")
public class HttpServletStudent extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    Session session = HibernateUtil.getSessionFactory().openSession();
    List<Student> students = session.createQuery("from Student", Student.class).list();
    session.close();

    resp.setContentType("application/json");
    PrintWriter out = resp.getWriter();
    out.print("[");
    for (int i = 0; i < students.size(); i++) {
      Student s = students.get(i);
      out.printf("{\"id\":%d,\"name\":\"%s\",\"age\":%d}",
          s.getId(), s.getName(), s.getAge());
      if (i < students.size() - 1) out.print(",");
    }
    out.print("]");
    out.println("\n<!-- 这是HttpServlet实现 -->");
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String name = req.getParameter("name");
    int age = Integer.parseInt(req.getParameter("age"));

    Student student = new Student();
    student.setName(name);
    student.setAge(age);

    Session session = HibernateUtil.getSessionFactory().openSession();
    Transaction tx = session.beginTransaction();
    session.persist(student);
    tx.commit();
    session.close();

    resp.setStatus(HttpServletResponse.SC_CREATED);
    resp.getWriter().println("<!-- 这是HttpServlet实现 -->");
  }

  @Override
  protected void doPut(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    int id = Integer.parseInt(req.getParameter("id"));
    String name = req.getParameter("name");
    int age = Integer.parseInt(req.getParameter("age"));

    Session session = HibernateUtil.getSessionFactory().openSession();
    Transaction tx = session.beginTransaction();

    Student student = session.get(Student.class, id);
    if (student != null) {
      student.setName(name);
      student.setAge(age);
      session.merge(student);
      tx.commit();
      resp.setStatus(HttpServletResponse.SC_OK);
    } else {
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
    session.close();
    resp.getWriter().println("<!-- 这是HttpServlet实现 -->");
  }

  @Override
  protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    int id = Integer.parseInt(req.getParameter("id"));

    Session session = HibernateUtil.getSessionFactory().openSession();
    Transaction tx = session.beginTransaction();

    Student student = session.get(Student.class, id);
    if (student != null) {
      session.remove(student);
      tx.commit();
      resp.setStatus(HttpServletResponse.SC_OK);
    } else {
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
    session.close();
    resp.getWriter().println("<!-- 这是HttpServlet实现 -->");
  }
}