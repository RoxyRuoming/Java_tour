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

// 使用Servlet接口实现的版本 - less abstract, more code, more manual implementation

/***
 * main differences between Servlet and HttpServlet:
 * 1. need to implement methods of Servlet interface: init(), service(), destroy(). getServletConfig(), getServletInfo()
 * 2. 手动handle requests， routes requests(to different method handlers, like doPost) manage Servlet life cycle, handle unsupported http methods
 * -- doGet, doPost, doPut, doDelete 方法内部/业务逻辑几乎一致
 */
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

  // 请求分发
  // manually cast req -> httpReq
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
        httpResp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED); // return 405
        httpResp.getWriter().println("<!-- 这是Servlet接口实现 -->");
        break;
    }
  }

  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    Session session = HibernateUtil.getSessionFactory().openSession(); // open hibernate session
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

    resp.setStatus(HttpServletResponse.SC_CREATED); // status code 201
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
      resp.setStatus(HttpServletResponse.SC_OK); // status code 200
    } else {
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND); // status code 404
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