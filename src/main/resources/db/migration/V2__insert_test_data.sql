-- Статусы заказов
INSERT INTO order_status (name) VALUES
('Создан'),
('Оплачен'),
('Доставлен'),
('Отменен')
ON CONFLICT DO NOTHING;


-- Товары
INSERT INTO product (description, price, quantity, category) VALUES
('Ноутбук Lenovo', 800.00, 10, 'Электроника'),
('Смартфон Samsung', 600.00, 15, 'Электроника'),
('Телевизор LG', 1200.00, 5, 'Электроника'),
('Холодильник Bosch', 900.00, 7, 'Бытовая техника'),
('Кофеварка Philips', 150.00, 20, 'Бытовая техника'),
('Книга Достоевский', 20.00, 50, 'Книги'),
('Настольная лампа', 35.00, 30, 'Освещение'),
('Монитор Dell', 300.00, 12, 'Электроника'),
('Наушники Sony', 100.00, 25, 'Аудио'),
('Клавиатура Logitech', 45.00, 40, 'Компьютерные аксессуары');


-- Покупатели
INSERT INTO customer (first_name, last_name, phone, email) VALUES
('Иван', 'Иванов', '+79990000001', 'ivanov@mail.com'),
('Петр', 'Петров', '+79990000002', 'petrov@mail.com'),
('Сергей', 'Сергеев', '+79990000003', 'sergeev@mail.com'),
('Анна', 'Сидорова', '+79990000004', 'sidorova@mail.com'),
('Ольга', 'Кузнецова', '+79990000005', 'kuz@mail.com'),
('Дмитрий', 'Смирнов', '+79990000006', 'smirnov@mail.com'),
('Мария', 'Федорова', '+79990000007', 'fedorova@mail.com'),
('Алексей', 'Попов', '+79990000008', 'popov@mail.com'),
('Наталья', 'Соколова', '+79990000009', 'sokolova@mail.com'),
('Юлия', 'Васильева', '+79990000010', 'vasilieva@mail.com');


-- Заказы
INSERT INTO orders (product_id, customer_id, order_date, quantity, status_id) VALUES
(1, 1, NOW() - INTERVAL '2 days', 1, 1),
(2, 2, NOW() - INTERVAL '1 days', 2, 2),
(3, 3, NOW() - INTERVAL '10 days', 1, 3),
(4, 4, NOW(), 1, 1),
(5, 5, NOW(), 2, 1),
(6, 6, NOW(), 3, 2),
(7, 7, NOW(), 1, 3),
(8, 8, NOW(), 2, 1),
(9, 9, NOW(), 1, 4),
(10, 10, NOW(), 1, 1);