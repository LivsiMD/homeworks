package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

public class SqlRunner {
    public static void run(Connection conn, String filePath) {
        System.out.println("=== Выполнение " + filePath + " ===");
        try {
            // Читаем SQL файл
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            // Разбиваем на отдельные запросы по ";"
            String[] queries = content.split(";");

            try (Statement stmt = conn.createStatement()) {
                for (String rawSql : queries) {
                    String sql = rawSql.trim();
                    if (sql.isEmpty()) continue;

                    System.out.println("\nSQL -> " + sql);

                    boolean hasResult = stmt.execute(sql);

                    if (hasResult) {
                        // SELECT-запрос
                        try (ResultSet rs = stmt.getResultSet()) {
                            ResultSetMetaData meta = rs.getMetaData();
                            int cols = meta.getColumnCount();

                            // Заголовки
                            for (int i = 1; i <= cols; i++) {
                                System.out.print(meta.getColumnName(i) + "\t");
                            }
                            System.out.println();

                            // Данные
                            while (rs.next()) {
                                for (int i = 1; i <= cols; i++) {
                                    System.out.print(rs.getString(i) + "\t");
                                }
                                System.out.println();
                            }
                        }
                    } else {
                        // UPDATE/DELETE/INSERT
                        int affected = stmt.getUpdateCount();
                        System.out.println("Rows affected: " + affected);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Ошибка SQL: " + e.getMessage());
        }
    }
}