import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//Создал класс
public class Person {
    private String name;
    private int money;
    private List<Product> bag = new ArrayList<>();

    //Конструктор класса
    public Person(String name, int money) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя не может быть пустым");
        }
        if (name.length() < 3) {
            throw new IllegalArgumentException("Имя не может быть короче 3 символов");
        }
        if (money < 0) {
            throw new IllegalArgumentException("Деньги не могут быть отрицательными");
        }
        this.name = name;
        this.money = money;
    }

    //Гетеры хотя часть и не использована, но по заданию надо было создать
    public String getName() {
        return name;
    }

    public int getMoney() {
        return money;
    }

    public List<Product> getBag() {
        return bag;
    }

    //Метод который проверяет может ли покупатель сделать покупку, в если денег достаточно - то вычитаем цену из денег покупателя, и пишем что купил, если не хватает денег, то пишем об этом.
    public boolean buyProduct(Product product) {
        if (product.getCost() > money) {
            System.out.println(name + " не может позволить себе " + product.getName());
            return false;
        } else {
            bag.add(product);
            money -= product.getCost();
            System.out.println(name + " купил " + product.getName() + " Цена " + product.getCost()); //дополнен ввывод ценной товара, для проверки работы скидок
            return true;
        }
    }

//Переопределение функций для ввывода покупателей и покупок, сравнения покупателей
    @Override
    public String toString() {
        if (bag.isEmpty()) {
            return name + " - Ничего не куплено";
        } else {
            StringBuilder result = new StringBuilder(name + " - ");
            for (int i = 0; i < bag.size(); i++) {
                result.append(bag.get(i).getName());
                if (i < bag.size() - 1) result.append(", ");
            }
            return result.toString();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person person = (Person) o;
        return money == person.money && name.equals(person.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, money);
    }
}