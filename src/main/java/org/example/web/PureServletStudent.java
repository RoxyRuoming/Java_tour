package org.example.web;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.model.Student;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

// 使用Servlet接口实现的版本
@WebServlet("/pure-students")
public class PureServletStudent implements Servlet {
  private ServletConfig config;

  @Override
  public void init(ServletConfig config) throws ServletException {
    this.config = config;
    System.out.println("PureServletStudent 初始化");
  }

  @Override
  public ServletConfig getServletConfig() {
    return config;
  }

  @Override
  public void service(ServletRequest req, ServletResponse resp)
      throws ServletException, IOException {
    HttpServletRequest httpReq = (HttpServletRequest) req;
    HttpServletResponse httpResp = (HttpServletResponse) resp;

    String method = httpReq.getMethod();

    switch (method) {
      case "GET":
        doGet(httpReq, httpResp);
        break;
      case "POST":
        doPost(httpReq, httpResp);
        break;
      case "PUT":
        doPut(httpReq, httpResp);
        break;
      case "DELETE":
        doDelete(httpReq, httpResp);
        break;
      default:
        httpResp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        httpResp.getWriter().println("<!-- 这是Servlet接口实现 -->");
        break;
    }
  }

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
    out.println("\n<!-- 这是Servlet接口实现 -->");
  }

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
    resp.getWriter().println("<!-- 这是Servlet接口实现 -->");
  }

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
    resp.getWriter().println("<!-- 这是Servlet接口实现 -->");
  }

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
    resp.getWriter().println("<!-- 这是Servlet接口实现 -->");
  }

  @Override
  public String getServletInfo() {
    return "Student Management Pure Servlet";
  }

  @Override
  public void destroy() {
    System.out.println("PureServletStudent 销毁");
    config = null;
  }
}