package uz.medsu.sevice;

import uz.medsu.payload.drugs.OrderDTO;
import uz.medsu.utils.ResponseMessage;

public interface OrderService {
    ResponseMessage createOrder(OrderDTO orderDTO);
    ResponseMessage cancelOrder(Long orderId, Boolean autoCancel);
    void basketClear(Long id);
}
