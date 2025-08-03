import java.util.Objects;

//Создал класс
public class Product {
    private String title;
    private int cost;

    //Конструктор класса
    public Product(String title, int cost) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Название не может быть пустым");
        }
        if (cost < 0) {
            throw new IllegalArgumentException("Стоимость продукта не может быть отрицательной");
        }
        this.title = title;
        this.cost = cost;
    }

    //Гетеры
    public String getName() {
        return title;
    }

    public int getCost() {
        return cost;
    }

//Переопределение функций для ввывода продукта. сравнения
    @Override
    public String toString() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return cost == product.cost && title.equals(product.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, cost);
    }
}