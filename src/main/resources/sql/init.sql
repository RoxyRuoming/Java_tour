DROP TABLE IF EXISTS students;

CREATE TABLE students (
                          id SERIAL PRIMARY KEY,
                          name VARCHAR(100) NOT NULL,
                          age INTEGER

--                       ssn
--                       birth
--                       credit card
-- create a DTO (id, name, age - useful info) to send a http request(payload) to the service1
);