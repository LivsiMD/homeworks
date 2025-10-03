-- ===== SELECT (чтение) =====
-- 1. Список всех заказов за последние 7 дней
SELECT o.id, c.first_name, p.description, o.quantity, o.order_date
FROM orders o
JOIN customer c ON o.customer_id = c.id
JOIN product p ON o.product_id = p.id
WHERE o.order_date >= NOW() - INTERVAL '7 days'
ORDER BY o.order_date DESC;

-- 2. Топ-3 самых популярных товара по количеству заказов
SELECT p.description, SUM(o.quantity) AS total_sold
FROM orders o
JOIN product p ON o.product_id = p.id
GROUP BY p.description
ORDER BY total_sold DESC
LIMIT 3;

-- 3. Общая сумма всех заказов
SELECT SUM(o.quantity * p.price) AS total_revenue
FROM orders o
JOIN product p ON o.product_id = p.id;

-- 4. Список покупателей и количество их заказов
SELECT c.first_name, c.last_name, COUNT(o.id) AS order_count
FROM customer c
LEFT JOIN orders o ON c.id = o.customer_id
GROUP BY c.first_name, c.last_name
ORDER BY order_count DESC;

-- 5. Список товаров, где остаток на складе < 10
SELECT id, description, quantity
FROM product
WHERE quantity < 10;

-- ===== UPDATE =====
-- 6. Увеличить количество конкретного товара на 5
UPDATE product SET quantity = quantity + 5 WHERE id = 1;

-- 7. Изменить email покупателя
UPDATE customer SET email = 'new_email@mail.com' WHERE id = 1;

-- 8. Обновить цену всех товаров категории "Компьютерные аксессуары" (повышение на 10%)
UPDATE product
SET price = price * 1.1
WHERE category = 'Компьютерные аксессуары';

-- ===== DELETE =====
-- 9. Удалить клиентов без заказов
DELETE FROM customer
WHERE id NOT IN (SELECT customer_id FROM orders);

-- 10. Удалить заказы старше 1 года
DELETE FROM orders
WHERE order_date < NOW() - INTERVAL '1 year';