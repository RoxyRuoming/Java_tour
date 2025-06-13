DROP TABLE IF EXISTS students;

CREATE TABLE students (
                          id SERIAL PRIMARY KEY,
                          name VARCHAR(100) NOT NULL,
                          age INTEGER,
                          ssn VARCHAR(20),
                          birth DATE,
                          credit_card_number VARCHAR(30)
);