package repository;

import model.Car;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс CarsRepository определяет методы для работы с коллекцией автомобилей
 * Реализация буду делать в классе CarsRepositoryImpl.
 */

public interface CarsRepository {
    List<Car> loadCars(String filePath);
    void saveResults(String filePath, String data);

    List<String> findNumbersByColorOrMileage(List<Car> cars, String color, long mileage);
    long countUniqueModelsInRange(List<Car> cars, long minCost, long maxCost);
    Optional<String> findColorOfMinCostCar(List<Car> cars);
    double averageCostByModel(List<Car> cars, String model);
}