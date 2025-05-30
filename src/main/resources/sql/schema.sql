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