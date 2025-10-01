-- Таблица товаров
CREATE TABLE IF NOT EXISTS product (
id SERIAL PRIMARY KEY,
description TEXT NOT NULL,
price NUMERIC(10,2) NOT NULL CHECK (price >= 0),
quantity INT NOT NULL CHECK (quantity >= 0),
category VARCHAR(100) NOT NULL
);


-- Таблица покупателей
CREATE TABLE IF NOT EXISTS customer (
id SERIAL PRIMARY KEY,
first_name VARCHAR(100) NOT NULL,
last_name VARCHAR(100) NOT NULL,
phone VARCHAR(20) NOT NULL UNIQUE,
email VARCHAR(100) NOT NULL UNIQUE
);


-- Таблица статусов заказов
CREATE TABLE IF NOT EXISTS order_status (
id SERIAL PRIMARY KEY,
name VARCHAR(50) NOT NULL UNIQUE
);


-- Таблица заказов
CREATE TABLE IF NOT EXISTS orders (
id SERIAL PRIMARY KEY,
product_id INT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
customer_id INT NOT NULL REFERENCES customer(id) ON DELETE CASCADE,
order_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
quantity INT NOT NULL CHECK (quantity > 0),
status_id INT NOT NULL REFERENCES order_status(id)
);


-- Индексы
CREATE INDEX IF NOT EXISTS idx_orders_product_id ON orders(product_id);
CREATE INDEX IF NOT EXISTS idx_orders_customer_id ON orders(customer_id);
CREATE INDEX IF NOT EXISTS idx_orders_date ON orders(order_date);