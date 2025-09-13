import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        List<Car> cars = Arrays.asList(
                new Car("a123me", "Mercedes", "White", 0, 8300000),
                new Car("b873of", "Volvo", "Black", 0, 673000),
                new Car("w487mn", "Lexus", "Grey", 76000, 900000),
                new Car("p987hj", "Volvo", "Red", 610, 704340),
                new Car("c987ss", "Toyota", "White", 254000, 761000),
                new Car("o983op", "Toyota", "Black", 698000, 740000),
                new Car("p146op", "BMW", "White", 271000, 850000),
                new Car("u893ii", "Toyota", "Purple", 210900, 440000),
                new Car("l097df", "Toyota", "Black", 108000, 780000),
                new Car("y876wd", "Toyota", "Black", 160000, 1000000)
        );

        String colorToFind = "Black";
        long mileageToFind = 0L;
        long n = 700_000L, m = 800_000L;
        String modelToFind = "Toyota";
        String modelToFind2 = "Volvo";

        System.out.println("Автомобили в базе:");
        System.out.println("Number  Model    Color   Mileage  Cost");
        cars.forEach(System.out::println);

        // 1. Номера автомобилей с цветом или пробегом
        List<String> numbers = cars.stream()
                .filter(c -> c.getColor().equalsIgnoreCase(colorToFind) || c.getMileage() == mileageToFind)
                .map(Car::getNumber)
                .collect(Collectors.toList());

        System.out.println("\nНомера автомобилей по цвету или пробегу: " + String.join(" ", numbers));

        // 2. Количество уникальных моделей в ценовом диапазоне
        long uniqueModels = cars.stream()
                .filter(c -> c.getCost() >= n && c.getCost() <= m)
                .map(Car::getModel)
                .distinct()
                .count();

        System.out.println("Уникальные автомобили: " + uniqueModels + " шт.");

        // 3. Цвет автомобиля с минимальной стоимостью
        String minCostColor = cars.stream()
                .min(Comparator.comparingLong(Car::getCost))
                .map(Car::getColor)
                .orElse("Нет данных");

        System.out.println("Цвет автомобиля с минимальной стоимостью: " + minCostColor);

        // 4. Средняя стоимость искомой модели
        double avgToyota = cars.stream()
                .filter(c -> c.getModel().equalsIgnoreCase(modelToFind))
                .mapToLong(Car::getCost)
                .average()
                .orElse(0);

        double avgVolvo = cars.stream()
                .filter(c -> c.getModel().equalsIgnoreCase(modelToFind2))
                .mapToLong(Car::getCost)
                .average()
                .orElse(0);

        System.out.printf("Средняя стоимость модели %s: %,.2f%n", modelToFind, avgToyota);
        System.out.printf("Средняя стоимость модели %s: %,.2f%n", modelToFind2, avgVolvo);
    }
}