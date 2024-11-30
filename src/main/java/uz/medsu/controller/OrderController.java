package uz.medsu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.medsu.sevice.OrderService;
import uz.medsu.utils.ResponseMessage;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/{id}")
    private ResponseEntity<ResponseMessage> addOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.createOrder(id));
    }

    @GetMapping("/{id}")
    private ResponseEntity<ResponseMessage> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrder(id));
    }

    @GetMapping
    private ResponseEntity<ResponseMessage> getOrders(Integer page, Integer size) {
        return ResponseEntity.ok(orderService.getOrders(page, size));
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<ResponseMessage> cancelOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.cancelOrder(id, false));
    }
}
