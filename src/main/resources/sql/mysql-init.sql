DROP TABLE IF EXISTS students;

CREATE TABLE students (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(100) NOT NULL,
                          age INTEGER,
                          ssn VARCHAR(20),
                          birth DATE,
                          credit_card_number VARCHAR(30)
);