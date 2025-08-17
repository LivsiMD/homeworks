import java.util.*;
import java.time.LocalDate;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Person> people = new ArrayList<>();
        Map<String, Product> products = new HashMap<>();

        // Ввод покупателей
        System.out.println("Введите покупателей (пример: Имя = Деньги; ...), пустая строка — завершение ввода:");

        //Данная конструкция позволяет организовать многостроковый ввод собирая строку из несколькоких и вставлять пробел между строк для разделения информации
        StringBuilder fullInput = new StringBuilder();
        while (true) {
            String line = scanner.nextLine();
            if (line.trim().isEmpty()) break;
            fullInput.append(line).append(" "); // добавляем пробел между строками
        }

        //Разбираем строку ввода по ; , что позвоняет нам вводить покупателей по шаблону (покупатель = деньги) строкой сразу несколько с разделителем
        String[] parsedEntries = fullInput.toString().split(";");
        for (String entry : parsedEntries) {
            String[] parts = entry.trim().split("=");
            if (parts.length != 2) {
                System.out.println("Неверный формат: " + entry.trim());
                continue;
            }

            String name = parts[0].trim();
            String moneyStr = parts[1].trim();

            //Блок try catch для того, что бы показатать пользователю явные и понятно ошибки ввовода
            try {
                int money = Integer.parseInt(moneyStr);
                people.add(new Person(name, money));
            }  catch (NumberFormatException e) {
                System.out.println("Ошибка в числе у " + name);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

// Ввод продуктов
        System.out.println("Введите продукты (пример: Хлеб = 40; Молоко = 60; если товар со скидкой формат - Товар = Цена:Процент скидки:Дата окончания скидки), пустая строка - конец:");

        //Данная конструкция позволяет организовать многостроковый ввод собирая строку из несколькоких и вставлять пробел между строк для разделения информации
        StringBuilder productInput = new StringBuilder();
        while (true) {
            String line = scanner.nextLine();
            if (line.trim().isEmpty()) break;
            productInput.append(line).append(" "); // добавляем пробел для разделения строк
        }

        //Разбираем строку ввода по ; для аналочной логики как в покупателях
        String[] productEntries = productInput.toString().split(";");
        for (String entry : productEntries) {
            String[] parts = entry.trim().split("=");
            if (parts.length != 2) {
                System.out.println("Неверный формат продукта: " + entry.trim());
                continue;
            }

            String productName = parts[0].trim();
            String costStr = parts[1].trim();

            try {
                Product product;
                if (costStr.contains(":")) {
                    // Формат скидочного продукта: цена:скидка:год-месяц-день
                    String[] vals = costStr.split(":");
                    if (vals.length != 3) {
                        throw new IllegalArgumentException("Неверный формат скидочного продукта: " + costStr);
                    }
                    int cost = Integer.parseInt(vals[0]);
                    int discount = Integer.parseInt(vals[1]);
                    LocalDate validUntil = LocalDate.parse(vals[2]);
                    product = new DiscountProduct(productName, cost, discount, validUntil);
                } else {
                    // Обычный продукт
                    int cost = Integer.parseInt(costStr);
                    product = new Product(productName, cost);
                }
                products.put(productName, product);
            } catch (Exception e) {
                System.out.println("Ошибка у продукта " + productName + ": " + e.getMessage());
            }
        }

        // Ввод покупок
        System.out.println("Введите покупки (пример: Анна Петровна - Хлеб), END для завершения:");
        while (true) {
            String line = scanner.nextLine();
            //Прерываем по END выполнение программы
            if (line.equalsIgnoreCase("END")) break;
            //В этом блоке происходит разбор строки с покупкой, поиск покупателя по имени, поиск продукта, и защищаем программу от некоректных ввоодв через try catch
            try {
                //Разбор строки с покупкой
                String[] parts = line.split(" - ");
                String personName = parts[0].trim();
                String productName = parts[1].trim();

                Person buyer = people.stream()
                        .filter(p -> p.getName().equals(personName))
                        .findFirst().orElse(null);
                Product product = products.get(productName);

                if (buyer == null || product == null) {
                    System.out.println("Неверные данные: " + line);
                    continue;
                }

                buyer.buyProduct(product);
            } catch (Exception e) {
                System.out.println("Ошибка покупки: " + e.getMessage());
            }
        }

        // Вывод результатов перебором через цикл for,
        for (Person person : people) {
            System.out.println(person);
        }
    }
}