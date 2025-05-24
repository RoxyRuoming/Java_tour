-- basic select queries
SELECT * FROM students;
SELECT first_name, last_name, grade FROM students;
SELECT * FROM students WHERE grade = 'A';
SELECT * FROM students WHERE age > 20;
SELECT * FROM students WHERE gpa BETWEEN 3.0 and 3.5;

-- sort and limit
SELECT * FROM students ORDER BY gpa DESC;
SELECT * FROM students ORDER BY gpa DESC LIMIT 3; -- top 3
SELECT * FROM students ORDER BY last_name ASC; -- by last name alphabetically

-- aggregation
SELECT COUNT(*) as total_students FROM students;
SELECT AVG(gpa) as average_gpa FROM students;
SELECT MAX(gpa) as highest_gpa, min(gpa) as lowest_gpa FROM students;
SELECT grade, COUNT(*) as student_count FROM students group by grade; -- Count students by grade
SELECT grade, AVG(gpa) as avg_gpa FROM students GROUP BY grade; -- Average GPA by grade

-- string functions and pattern matching
SELECT * FROM students WHERE first_name LIKE 'A%';
SELECT * FROM students WHERE last_name LIKE '%son%';
SELECT CONCAT(first_name, ' ', last_name) as full_name FROM students;

-- product queries
SELECT category, AVG(price) as avg_price FROM products GROUP BY category;

-- ========================================
-- JOIN QUERIES (More Advanced)
-- ========================================

-- 28. Orders with customer information
SELECT o.order_id, o.order_date, o.total_amount,
       c.first_name, c.last_name, c.email
FROM orders o
         JOIN customers c ON o.customer_id = c.customer_id;

-- Employee interview question
-- Classic interview query: count employees per department
SELECT department, COUNT(*) AS headcount
FROM employees
GROUP BY department
ORDER BY department;
