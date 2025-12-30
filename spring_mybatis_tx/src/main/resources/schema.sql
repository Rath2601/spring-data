CREATE TABLE account (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100),
    balance INT
);

CREATE TABLE transaction_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id BIGINT,
    amount INT,
    status VARCHAR(50)
);
