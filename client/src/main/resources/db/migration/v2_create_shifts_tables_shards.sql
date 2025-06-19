CREATE TABLE shifts (
    id SERIAL PRIMARY KEY,
    request_id INTEGER NOT NULL,
    company_id VARCHAR(100) NOT NULL,
    user_id VARCHAR(100) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    activity VARCHAR(20) DEFAULT 'add',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);