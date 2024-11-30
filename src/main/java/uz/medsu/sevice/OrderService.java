package uz.medsu.sevice;

import uz.medsu.utils.ResponseMessage;

public interface OrderService {
    ResponseMessage createOrder(Long id);
    ResponseMessage cancelOrder(Long orderId, Boolean autoCancel);
    void basketClear(Long id);

    ResponseMessage getOrders(Integer page, Integer size);

    ResponseMessage getOrder(Long id);
}
