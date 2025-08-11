import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//Создание класса покупателя - персоны
public class Person {
    private String name;
    private int money;
    //список для создания корзины покупателя
    private List<Product> bag;

    //Ннеобходимые проверки
    public Person(String name, int money) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя не может быть пустым");
        }
        if (money < 0) {
            throw new IllegalArgumentException("Деньги не могут быть отрицательными");
        }
        this.name = name;
        this.money = money;
        this.bag = new ArrayList<>();
    }

    //гетеры для доступа к приватным полям
    public String getName() {
        return name;
    }

    public int getMoney() {
        return money;
    }

    public List<Product> getBag() {
        return bag;
    }

    //логика покупки продукта
    public boolean buyProduct(Product product) {
        //денег меньше чем есть у покупателя - не помжет себе позволить. проверка из условий задачи
        if (product.getCost() > money) {
            System.out.println(name + " не может позволить себе " + product.getTitle());
            return false;
        } else {
            //добавление в корзину покукупки
            bag.add(product);
            //вычитаем деньги из денег покупателя
            money -= product.getCost();
            //выводим что купил
            System.out.println(name + " купил " + product.getTitle());
            return true;
        }
    }

    //переназначение методов требуемое в задаче
    @Override
    public String toString() {
        if (bag.isEmpty()) {
            return name + " - Ничего не куплено";
        }
        return name + " - " + bag.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person person = (Person) o;
        return money == person.money &&
                name.equals(person.name) &&
                bag.equals(person.bag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, money, bag);
    }
}