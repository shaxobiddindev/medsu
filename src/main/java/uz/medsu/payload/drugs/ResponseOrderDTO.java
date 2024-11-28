package uz.medsu.payload.drugs;

import uz.medsu.entity.DrugClone;
import uz.medsu.entity.Invoice;
import uz.medsu.enums.OrderStatus;

import java.util.List;

public record ResponseOrderDTO(
        Long orderId,
        List<DrugClone> drugs,
        Double latitude,
        Double longitude,
        Double totalPrice,
        Long invoiceId
) {
}
