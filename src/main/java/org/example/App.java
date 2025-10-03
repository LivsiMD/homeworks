package org.example;

import org.flywaydb.core.Flyway;
import java.sql.*;
import java.util.Properties;
import java.io.InputStream;
import java.io.FileNotFoundException;


public class App {
    public static void main(String[] args) {
        Properties props = new Properties();
        try (InputStream fis = App.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (fis == null) throw new FileNotFoundException("application.properties not found");
            props.load(fis);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }


        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");


// Flyway миграции
        Flyway flyway = Flyway.configure().dataSource(url, user, password).load();
        var result = flyway.migrate();

// Визуализация результата миграций
        System.out.println("\nРезультат миграции базы данных:");
        System.out.println("+----------------------+----------------------+");
        System.out.printf("| %-20s | %-20s |\n", "Миграций выполнено", "Версия схемы");
        System.out.println("+----------------------+----------------------+");
        System.out.printf("| %-20d | %-20s |\n",
                result.migrationsExecuted,
                result.targetSchemaVersion != null ? result.targetSchemaVersion : "неизвестно");
        System.out.println("+----------------------+----------------------+\n");

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            conn.setAutoCommit(false);


            try {
// 1. Вставка товара
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO product(description, price, quantity, category) VALUES (?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {

                    String description = "Игровая мышь Razer";
                    double price = 75.0;
                    int quantity = 15;
                    String category = "Компьютерные аксессуары";

                    ps.setString(1, description);
                    ps.setDouble(2, price);
                    ps.setInt(3, quantity);
                    ps.setString(4, category);

                    int rows = ps.executeUpdate();

                    if (rows > 0) {
                        // Получение ID
                        try (ResultSet rs = ps.getGeneratedKeys()) {
                            int id = -1;
                            if (rs.next()) {
                                id = rs.getInt(1);
                            }

                            // Красивый вывод таблицей
                            System.out.println("\nТовар успешно добавлен в БД:");
                            System.out.println("+----+-------------------+--------+----------+-------------------------+");
                            System.out.printf("| %-2s | %-17s | %-6s | %-8s | %-23s |\n",
                                    "ID", "Description", "Price", "Quantity", "Category");
                            System.out.println("+----+-------------------+--------+----------+-------------------------+");
                            System.out.printf("| %-2d | %-17s | %-6.2f | %-8d | %-23s |\n",
                                    id, description, price, quantity, category);
                            System.out.println("+----+-------------------+--------+----------+-------------------------+\n");
                        }
                    } else {
                        System.out.println("Вставка не удалась.");
                    }
                }


// 2. Вставка покупателя
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO customer(first_name, last_name, phone, email) VALUES (?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {

                    String firstName = "Тест";
                    String lastName = "Покупатель";
                    String phone = "+79992223344";
                    String email = "testbuyer@mail.com";

                    ps.setString(1, firstName);
                    ps.setString(2, lastName);
                    ps.setString(3, phone);
                    ps.setString(4, email);

                    int rows = ps.executeUpdate();

                    if (rows > 0) {
                        try (ResultSet rs = ps.getGeneratedKeys()) {
                            int id = -1;
                            if (rs.next()) id = rs.getInt(1);

                            System.out.println("\nПокупатель успешно добавлен:");
                            System.out.println("+----+------------+-------------+--------------+-----------------------+");
                            System.out.printf("| %-2s | %-10s | %-11s | %-12s | %-21s |\n",
                                    "ID", "FirstName", "LastName", "Phone", "Email");
                            System.out.println("+----+------------+-------------+--------------+-----------------------+");
                            System.out.printf("| %-2d | %-10s | %-11s | %-12s | %-21s |\n",
                                    id, firstName, lastName, phone, email);
                            System.out.println("+----+------------+-------------+--------------+-----------------------+\n");
                        }
                    }
                }


// 3. Чтение последних 5 заказов
                try (Statement st = conn.createStatement()) {
                    ResultSet rs = st.executeQuery(
                            "SELECT o.id, c.first_name, p.description, o.quantity, o.order_date " +
                                    "FROM orders o " +
                                    "JOIN customer c ON o.customer_id = c.id " +
                                    "JOIN product p ON o.product_id = p.id " +
                                    "ORDER BY o.order_date DESC LIMIT 5");

                    System.out.println("\nПоследние 5 заказов:");
                    System.out.println("+----+------------+----------------------+----------+---------------------+");
                    System.out.printf("| %-2s | %-10s | %-20s | %-8s | %-19s |\n",
                            "ID", "Customer", "Product", "Qty", "Date");
                    System.out.println("+----+------------+----------------------+----------+---------------------+");

                    while (rs.next()) {
                        System.out.printf("| %-2d | %-10s | %-20s | %-8d | %-19s |\n",
                                rs.getInt("id"),
                                rs.getString("first_name"),
                                rs.getString("description"),
                                rs.getInt("quantity"),
                                rs.getTimestamp("order_date"));
                    }

                    System.out.println("+----+------------+----------------------+----------+---------------------+\n");
                }


// 4. Обновление цены товара
                try (PreparedStatement ps = conn.prepareStatement("UPDATE product SET price = ? WHERE id = 1")) {
                    double newPrice = 850.0;
                    ps.setDouble(1, newPrice);
                    int rows = ps.executeUpdate();

                    if (rows > 0) {
                        System.out.println("\nЦена товара с ID=1 успешно обновлена:");
                        System.out.println("+----+--------+");
                        System.out.printf("| %-2s | %-6s |\n", "ID", "Price");
                        System.out.println("+----+--------+");
                        System.out.printf("| %-2d | %-6.2f |\n", 1, newPrice);
                        System.out.println("+----+--------+\n");
                    }
                }


// 5. Удаление тестовых записей
                try (Statement st = conn.createStatement()) {
                    int rows = st.executeUpdate("DELETE FROM customer WHERE email='testbuyer@mail.com'");
                    System.out.println("\nУдаление тестовых записей:");
                    System.out.println("+-------------------------------+");
                    System.out.printf("| %-29s |\n", "Удалено записей: " + rows);
                    System.out.println("+-------------------------------+\n");
                }

                // Результаты выполнения test-queries.sql в IDE в консоль по условиям задачи
                System.out.println("\n=== Выполнение test-queries.sql ===");
                SqlRunner.run(conn, "src/main/resources/test-queries.sql");

                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}