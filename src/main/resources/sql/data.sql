-- data.sql - Insert sample data for practice

-- Insert Students data
INSERT INTO students (student_id, first_name, last_name, email, age, grade, enrollment_date, gpa) VALUES
                                                                                                      (1, 'Alice', 'Johnson', 'alice.johnson@email.com', 20, 'A', '2023-09-01', 3.85),
                                                                                                      (2, 'Bob', 'Smith', 'bob.smith@email.com', 19, 'B', '2023-09-01', 3.20),
                                                                                                      (3, 'Charlie', 'Brown', 'charlie.brown@email.com', 21, 'A', '2022-09-01', 3.90),
                                                                                                      (4, 'Diana', 'Wilson', 'diana.wilson@email.com', 18, 'C', '2024-01-15', 2.75),
                                                                                                      (5, 'Eva', 'Davis', 'eva.davis@email.com', 22, 'B', '2021-09-01', 3.45),
                                                                                                      (6, 'Frank', 'Miller', 'frank.miller@email.com', 20, 'A', '2023-01-15', 3.70),
                                                                                                      (7, 'Grace', 'Taylor', 'grace.taylor@email.com', 19, 'B', '2023-09-01', 3.25),
                                                                                                      (8, 'Henry', 'Anderson', 'henry.anderson@email.com', 21, 'C', '2022-01-15', 2.80);

-- Insert Products data
INSERT INTO products (product_id, product_name, category, price, stock_quantity, created_date) VALUES
                                                                                                   (1, 'Laptop Pro', 'Electronics', 1299.99, 50, '2024-01-01'),
                                                                                                   (2, 'Wireless Mouse', 'Electronics', 29.99, 200, '2024-01-01'),
                                                                                                   (3, 'Office Chair', 'Furniture', 199.99, 75, '2024-01-15'),
                                                                                                   (4, 'Notebook Set', 'Stationery', 15.99, 300, '2024-02-01'),
                                                                                                   (5, 'Coffee Mug', 'Kitchen', 12.99, 150, '2024-02-01'),
                                                                                                   (6, 'Desk Lamp', 'Electronics', 45.99, 80, '2024-01-20'),
                                                                                                   (7, 'Backpack', 'Accessories', 79.99, 60, '2024-01-10'),
                                                                                                   (8, 'Water Bottle', 'Kitchen', 18.99, 120, '2024-02-05');

-- Insert Customers data
INSERT INTO customers (customer_id, first_name, last_name, email, phone, city, country, registration_date) VALUES
                                                                                                               (1, 'John', 'Doe', 'john.doe@email.com', '555-0101', 'New York', 'USA', '2023-05-15'),
                                                                                                               (2, 'Jane', 'Smith', 'jane.smith@email.com', '555-0102', 'Los Angeles', 'USA', '2023-06-20'),
                                                                                                               (3, 'Mike', 'Johnson', 'mike.johnson@email.com', '555-0103', 'Chicago', 'USA', '2023-07-10'),
                                                                                                               (4, 'Sarah', 'Williams', 'sarah.williams@email.com', '555-0104', 'Houston', 'USA', '2023-08-05'),
                                                                                                               (5, 'David', 'Brown', 'david.brown@email.com', '555-0105', 'Phoenix', 'USA', '2023-09-12'),
                                                                                                               (6, 'Lisa', 'Davis', 'lisa.davis@email.com', '555-0106', 'Philadelphia', 'USA', '2023-10-18'),
                                                                                                               (7, 'Tom', 'Wilson', 'tom.wilson@email.com', '555-0107', 'San Antonio', 'USA', '2023-11-22'),
                                                                                                               (8, 'Amy', 'Moore', 'amy.moore@email.com', '555-0108', 'San Diego', 'USA', '2023-12-03');

-- Insert Orders data
INSERT INTO orders (order_id, customer_id, product_id, quantity, order_date, total_amount, status) VALUES
                                                                                                       (1, 1, 1, 1, '2024-01-15', 1299.99, 'Completed'),
                                                                                                       (2, 1, 2, 2, '2024-01-15', 59.98, 'Completed'),
                                                                                                       (3, 2, 3, 1, '2024-01-20', 199.99, 'Shipped'),
                                                                                                       (4, 3, 4, 5, '2024-01-25', 79.95, 'Completed'),
                                                                                                       (5, 3, 5, 2, '2024-01-25', 25.98, 'Completed'),
                                                                                                       (6, 4, 6, 1, '2024-02-01', 45.99, 'Processing'),
                                                                                                       (7, 5, 7, 1, '2024-02-05', 79.99, 'Shipped'),
                                                                                                       (8, 6, 8, 3, '2024-02-10', 56.97, 'Completed'),
                                                                                                       (9, 2, 1, 1, '2024-02-12', 1299.99, 'Processing'),
                                                                                                       (10, 7, 2, 4, '2024-02-15', 119.96, 'Completed');


-- Insert Employees data
INSERT INTO employees (id, name, department) VALUES
                                                 (1, 'Alice', 'Engineering'),
                                                 (2, 'Bob', 'HR'),
                                                 (3, 'Charlie', 'Engineering'),
                                                 (4, 'Diana', 'HR'),
                                                 (5, 'Eva', 'Marketing');
