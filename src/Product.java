import java.util.Objects;

//Создание класса продукта
public class Product {
    private String title;
    private int cost;

    //Требуемые проверки
    public Product(String title, int cost) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Название продукта не может быть пустым");
        }
        if (cost < 0) {
            throw new IllegalArgumentException("Стоимость продукта не может быть отрицательной");
        }
        this.title = title;
        this.cost = cost;
    }

    //гетеры для получения полей
    public String getTitle() {
        return title;
    }

    public int getCost() {
        return cost;
    }

    //Требуемые по заданию переназначения методы
    @Override
    public String toString() {
        return title + " (" + cost + " руб.)";
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