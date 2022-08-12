CREATE TABLE IF NOT EXISTS expenses(
    id VARCHAR(8) PRIMARY KEY,
    expenseBool BOOLEAN,
    date DATE,
    amount INT,
    category VARCHAR(16),
    account VARCHAR(16),
    memo VARCHAR(256)
);