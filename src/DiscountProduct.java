import java.time.LocalDate;

public class DiscountProduct extends Product {
    private int discount; // размер скидки в %
    private LocalDate validUntil; // дата, до которой действует скидка

    public DiscountProduct(String title, int cost, int discount, LocalDate validUntil) {
        super(title, cost); // вызываем конструктор родителя Product для валидации

        if (discount <= 0 || discount >= 100) {
            throw new IllegalArgumentException("Скидка должна быть от 1 до 99%");
        }
        if (validUntil == null) {
            throw new IllegalArgumentException("Срок действия скидки должен быть указан");
        }

        this.discount = discount;
        this.validUntil = validUntil;
    }

    // Получаем цену с учётом скидки
    @Override
    public int getCost() {
        if (LocalDate.now().isAfter(validUntil)) {
            return super.getCost(); // скидка истекла → цена обычная
        }
        return super.getCost() - (super.getCost() * discount / 100);
    }

    public int getDiscount() {
        return discount;
    }

    public LocalDate getValidUntil() {
        return validUntil;
    }

    @Override
    public String toString() {
        if (LocalDate.now().isAfter(validUntil)) {
            return getName() + " (без скидки, цена " + super.getCost() + ")";
        }
        return getName() + " (со скидкой " + discount + "%, цена " + getCost() + ")";
    }
}