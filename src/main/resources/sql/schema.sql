-- schema.sql
-- create database tables
-- drop table if it exists (reset)
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS employees;

-- Create Students table for basic practice
CREATE TABLE students (
                          student_id INT PRIMARY KEY,
                          first_name VARCHAR(50) NOT NULL,
                          last_name VARCHAR(50) NOT NULL,
                          email VARCHAR(100) UNIQUE,
                          age INT,
                          grade CHAR(1),
                          enrollment_date DATE,
                          gpa DECIMAL(3,2) -- exception?
);

-- Create Products table
CREATE TABLE products (
                          product_id INT PRIMARY KEY,
                          product_name VARCHAR(100) NOT NULL,
                          category VARCHAR(50),
                          price DECIMAL(10,2),
                          stock_quantity INT,
                          created_date DATE
);

-- Create Customers table
CREATE TABLE customers (
                           customer_id INT PRIMARY KEY,
                           first_name VARCHAR(50) NOT NULL,
                           last_name VARCHAR(50) NOT NULL,
                           email VARCHAR(100) UNIQUE,
                           phone VARCHAR(20),
                           city VARCHAR(50),
                           country VARCHAR(50),
                           registration_date DATE
);

-- create Orders table (with foreign key to customer)
CREATE TABLE orders (
                        order_id INT PRIMARY KEY,
                        customer_id INT,
                        product_id INT,
                        quantity INT,
                        order_date DATE,
                        total_amount DECIMAL(10,2),
                        status VARCHAR(20),
                        FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
                        FOREIGN KEY (product_id) REFERENCES products(product_id)

);

-- create Employee table (to practice the classic employee sql interview question)
CREATE TABLE employees (
                           id INT PRIMARY KEY,
                           name VARCHAR(100),
                           department VARCHAR(100)
);


-- trigger, stored procedure practice

-- 先删除可能存在的对象
DROP TRIGGER IF EXISTS student_audit_trigger ON students;
DROP FUNCTION IF EXISTS student_audit_func() CASCADE;
DROP PROCEDURE IF EXISTS update_student_grade(INT, CHAR) CASCADE;
DROP FUNCTION IF EXISTS get_students_by_grade(CHAR) CASCADE;
DROP TABLE IF EXISTS students_audit;

-- 创建审计表
CREATE TABLE students_audit (
                                id SERIAL PRIMARY KEY,
                                action VARCHAR(10),
                                student_id INT,
                                audit_time TIMESTAMP
);

-- 创建触发器函数
CREATE FUNCTION student_audit_func()
    RETURNS TRIGGER AS $body$
BEGIN
    INSERT INTO students_audit(action, student_id, audit_time)
    VALUES(TG_OP, NEW.student_id, NOW());
    RETURN NEW;
END;
$body$ LANGUAGE plpgsql;

-- 创建触发器
CREATE TRIGGER student_audit_trigger
    AFTER INSERT ON students
    FOR EACH ROW EXECUTE PROCEDURE student_audit_func();

-- 创建存储过程
CREATE OR REPLACE PROCEDURE update_student_grade(
    student_id_param INT,
    new_grade_param CHAR
)
    LANGUAGE plpgsql
AS $procedure$
BEGIN
    UPDATE students
    SET grade = new_grade_param
    WHERE student_id = student_id_param;
END;
$procedure$;

-- 修改后的存储函数，解决列名歧义问题
CREATE OR REPLACE FUNCTION get_students_by_grade(grade_param CHAR)
    RETURNS TABLE (
                      id INT,
                      name VARCHAR,
                      student_grade CHAR  -- 更改返回列名
                  ) AS $function$
BEGIN
    RETURN QUERY
        SELECT student_id, first_name, students.grade AS student_grade
        FROM students
        WHERE students.grade = grade_param;
END;
$function$ LANGUAGE plpgsql;