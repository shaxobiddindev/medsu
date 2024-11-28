package uz.medsu.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import uz.medsu.entity.Basket;
import uz.medsu.entity.DrugOrder;

@Getter
public class BasketClearEvent extends ApplicationEvent {
    private final Basket basket;

    public BasketClearEvent(Basket basket) {
        super(basket);
        this.basket = basket;
    }
}
