-- 修改用户表定义，密码允许为 null
CREATE TABLE IF NOT EXISTS users (
                                     id SERIAL PRIMARY KEY,
                                     username VARCHAR(255) NOT NULL UNIQUE,
                                     password VARCHAR(255), -- 移除 NOT NULL 约束
                                     email VARCHAR(255) UNIQUE,
                                     name VARCHAR(255),
                                     provider VARCHAR(255),
                                     provider_id VARCHAR(255)
);

-- 创建用户角色表（如果不存在）
CREATE TABLE IF NOT EXISTS user_roles (
                                          user_id BIGINT NOT NULL,
                                          role VARCHAR(255) NOT NULL,
                                          PRIMARY KEY (user_id, role),
                                          FOREIGN KEY (user_id) REFERENCES users (id)
);

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