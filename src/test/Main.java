package test;

import model.Car;
import repository.CarsRepository;
import repository.CarsRepositoryImpl;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        CarsRepository repo = new CarsRepositoryImpl();

        // Пути к файлам
        String inputFile = "src/data/cars.txt"; // файл со списком машин
        String outputFile = "src/data/results.txt"; //вывод сделан в отдельный фаил, тк в задании не указано как должно быть реализовано, выбрал в пользу нового файла

        // Параметры
        String colorToFind = "Black";
        long mileageToFind = 0L;
        long n = 700_000L, m = 800_000L;
        String modelToFind = "Toyota";
        String modelToFind2 = "Volvo";

        // Загружаем
        List<Car> cars = repo.loadCars(inputFile);

        StringBuilder result = new StringBuilder();
        result.append("Автомобили в базе:\n");
        result.append("Number  Model    Color   Mileage  Cost\n");
        cars.forEach(c -> result.append(c).append("\n"));

        //Выводы результатов
        //Найти номера автомобилей по цвету или пробегу
        List<String> numbers = repo.findNumbersByColorOrMileage(cars, colorToFind, mileageToFind);
        result.append("\nНомера автомобилей по цвету или пробегу: ")
                .append(String.join(" ", numbers))
                .append("\n");

        //Подсчитать уникальные модели в ценовом диапазоне
        long uniqueModels = repo.countUniqueModelsInRange(cars, n, m);
        result.append("Уникальные автомобили: ").append(uniqueModels).append(" шт.\n");

        //Вывести цвет автомобиля с минимальной стоимостью
        String minCostColor = repo.findColorOfMinCostCar(cars).orElse("Нет данных");
        result.append("Цвет автомобиля с минимальной стоимостью: ").append(minCostColor).append("\n");

        //Средняя стоимость модели
        double avgToyota = repo.averageCostByModel(cars, modelToFind);
        double avgVolvo = repo.averageCostByModel(cars, modelToFind2);

        result.append(String.format("Средняя стоимость модели %s: %,.2f%n", modelToFind, avgToyota));
        result.append(String.format("Средняя стоимость модели %s: %,.2f%n", modelToFind2, avgVolvo));

        // Сохраняем результат
        repo.saveResults(outputFile, result.toString());

        System.out.println("Результаты сохранены в " + outputFile);
    }
}
