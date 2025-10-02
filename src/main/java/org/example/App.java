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

        System.out.println("URL: '" + url + "'");
        System.out.println("User: '" + user + "'");
        System.out.println("Password: '" + password + "'");


// Flyway миграции
        Flyway flyway = Flyway.configure().dataSource(url, user, password).load();
        flyway.migrate();


        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            conn.setAutoCommit(false);


            try {
// 1. Вставка товара
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO product(description, price, quantity, category) VALUES (?, ?, ?, ?)")) {
                    ps.setString(1, "Игровая мышь Razer");
                    ps.setDouble(2, 75.0);
                    ps.setInt(3, 15);
                    ps.setString(4, "Компьютерные аксессуары");
                    ps.executeUpdate();
                }


// 2. Вставка покупателя
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO customer(first_name, last_name, phone, email) VALUES (?, ?, ?, ?)")) {
                    ps.setString(1, "Тест");
                    ps.setString(2, "Покупатель");
                    ps.setString(3, "+79992223344");
                    ps.setString(4, "testbuyer@mail.com");
                    ps.executeUpdate();
                }


// 3. Чтение последних 5 заказов
                try (Statement st = conn.createStatement()) {
                    ResultSet rs = st.executeQuery(
                            "SELECT o.id, c.first_name, p.description, o.quantity, o.order_date " +
                                    "FROM orders o " +
                                    "JOIN customer c ON o.customer_id = c.id " +
                                    "JOIN product p ON o.product_id = p.id " +
                                    "ORDER BY o.order_date DESC LIMIT 5");


                    while (rs.next()) {
                        System.out.printf("Order #%d: %s купил %s (%d шт.) %s\n",
                                rs.getInt("id"),
                                rs.getString("first_name"),
                                rs.getString("description"),
                                rs.getInt("quantity"),
                                rs.getTimestamp("order_date"));
                    }
                }


// 4. Обновление цены товара
                try (PreparedStatement ps = conn.prepareStatement("UPDATE product SET price = ? WHERE id = 1")) {
                    ps.setDouble(1, 850.0);
                    ps.executeUpdate();
                }


// 5. Удаление тестовых записей
                try (Statement st = conn.createStatement()) {
                    st.executeUpdate("DELETE FROM customer WHERE email='testbuyer@mail.com'");
                }


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