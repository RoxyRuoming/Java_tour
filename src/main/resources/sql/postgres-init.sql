-- 创建学生表（如果不存在）
CREATE TABLE IF NOT EXISTS students (
                                        id SERIAL PRIMARY KEY,
                                        name VARCHAR(255),
                                        age INTEGER,
                                        ssn VARCHAR(255),
                                        birth DATE,
                                        credit_card_number VARCHAR(255)
);

-- 创建用户表（如果不存在）
CREATE TABLE IF NOT EXISTS users (
                                     id SERIAL PRIMARY KEY,
                                     username VARCHAR(255) NOT NULL UNIQUE,
                                     password VARCHAR(255) NOT NULL
);

-- 创建用户角色表（如果不存在）
CREATE TABLE IF NOT EXISTS user_roles (
                                          user_id BIGINT NOT NULL,
                                          role VARCHAR(255) NOT NULL,
                                          PRIMARY KEY (user_id, role),
                                          FOREIGN KEY (user_id) REFERENCES users (id)
);

-- 添加测试学生数据（如果不存在）
INSERT INTO students (name, age, ssn, birth, credit_card_number)
SELECT 'Alice Smith', 20, '123-45-6789', '2003-01-15', '4111-1111-1111-1111'
WHERE NOT EXISTS (SELECT 1 FROM students WHERE name = 'Alice Smith');

INSERT INTO students (name, age, ssn, birth, credit_card_number)
SELECT 'Bob Johnson', 22, '987-65-4321', '2001-05-10', '5555-5555-5555-4444'
WHERE NOT EXISTS (SELECT 1 FROM students WHERE name = 'Bob Johnson');

INSERT INTO students (name, age, ssn, birth, credit_card_number)
SELECT 'Charlie Brown', 19, '456-78-9012', '2004-09-28', '3782-8224-6310-005'
WHERE NOT EXISTS (SELECT 1 FROM students WHERE name = 'Charlie Brown');

-- 检查并创建admin用户（如果不存在）
INSERT INTO users (username, password)
SELECT 'admin', '$2a$10$EqKMCKxp0XrUCSJUPY.JeubIW0T3Cb6JQZVlXYJy3ydvB7xWzWjOe'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');

-- 给admin用户添加ADMIN角色（如果用户存在）
INSERT INTO user_roles (user_id, role)
SELECT id, 'ROLE_ADMIN' FROM users
WHERE username = 'admin'
  AND NOT EXISTS (
    SELECT 1 FROM user_roles
    WHERE user_id = (SELECT id FROM users WHERE username = 'admin')
      AND role = 'ROLE_ADMIN'
);

-- 检查并创建普通用户（如果不存在）
INSERT INTO users (username, password)
SELECT 'user', '$2a$10$EqKMCKxp0XrUCSJUPY.JeubIW0T3Cb6JQZVlXYJy3ydvB7xWzWjOe'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'user');

-- 给普通用户添加USER角色（如果用户存在）
INSERT INTO user_roles (user_id, role)
SELECT id, 'ROLE_USER' FROM users
WHERE username = 'user'
  AND NOT EXISTS (
    SELECT 1 FROM user_roles
    WHERE user_id = (SELECT id FROM users WHERE username = 'user')
      AND role = 'ROLE_USER'
);