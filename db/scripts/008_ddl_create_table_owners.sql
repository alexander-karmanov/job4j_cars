CREATE TABLE IF NOT EXISTS owners (
    id SERIAL PRIMARY KEY,
    name TEXT,
    user_id INT NOT NULL UNIQUE REFERENCES auto_user(id)
);