package repository;

import model.Car;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Класс CarsRepositoryImpl реализует интерфейс CarsRepository.
 * Автомобили хранятся в текстовом файле cars.txt из условий задачи
 */

public class CarsRepositoryImpl implements CarsRepository {

    //Загружает список автомобилей из текстового файла
    @Override
    public List<Car> loadCars(String filePath) {
        try {
            return Files.lines(Paths.get(filePath))
                    .map(line -> line.split("\\|"))
                    .map(parts -> new Car(
                            parts[0],
                            parts[1],
                            parts[2],
                            Long.parseLong(parts[3]),
                            Long.parseLong(parts[4])
                    ))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения файла: " + filePath, e);
        }
    }


    //Сохраням результат
    @Override
    public void saveResults(String filePath, String data) {
        try {
            Files.write(Paths.get(filePath), data.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Ошибка записи файла: " + filePath, e);
        }
    }

    //Поиск номера автомобилей по условию
    @Override
    public List<String> findNumbersByColorOrMileage(List<Car> cars, String color, long mileage) {
        return cars.stream()
                .filter(c -> c.getColor().equalsIgnoreCase(color) || c.getMileage() == mileage)
                .map(Car::getNumber)
                .collect(Collectors.toList());
    }

    //Считаем количество уникальных моделей в заданном ценовом диапазоне
    @Override
    public long countUniqueModelsInRange(List<Car> cars, long minCost, long maxCost) {
        return cars.stream()
                .filter(c -> c.getCost() >= minCost && c.getCost() <= maxCost)
                .map(Car::getModel)
                .distinct()
                .count();
    }

    //Находим цвет автомобиля с минимальной стоимостью
    @Override
    public Optional<String> findColorOfMinCostCar(List<Car> cars) {
        return cars.stream()
                .min(Comparator.comparingLong(Car::getCost))
                .map(Car::getColor);
    }

    //Вычисляем среднюю стоимость автомобилей указанной модели
    @Override
    public double averageCostByModel(List<Car> cars, String model) {
        return cars.stream()
                .filter(c -> c.getModel().equalsIgnoreCase(model))
                .mapToLong(Car::getCost)
                .average()
                .orElse(0);
    }
}