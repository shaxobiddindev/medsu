package uz.medsu.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import uz.medsu.entity.DrugOrder;

@Getter
public class OrderCreatedEvent extends ApplicationEvent {
    private final DrugOrder order;

    public OrderCreatedEvent(DrugOrder order) {
        super(order);
        this.order = order;
    }
}
