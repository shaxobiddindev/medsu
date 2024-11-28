package uz.medsu.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import uz.medsu.event.*;
import uz.medsu.payload.EmailMessage;
import uz.medsu.sevice.AuthService;
import uz.medsu.sevice.EmailService;
import uz.medsu.sevice.OrderService;
import uz.medsu.sevice.UserService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class MyEventListener {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    private final TaskScheduler scheduler;
    @Autowired
    private AuthService authService;

    public MyEventListener() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(10);
        taskScheduler.initialize();
        this.scheduler = taskScheduler;
    }

    @EventListener
    @Async
    public void onOrderCreated(OrderCreatedEvent event) {
        LocalDateTime cancelTime = LocalDateTime.now().plusHours(24);
        Instant instant = cancelTime.atZone(ZoneId.systemDefault()).toInstant();
        scheduler.schedule(() -> orderService.cancelOrder(event.getOrder().getId(), true), instant);
    }

    @EventListener
    @Async
    public void onAppointmentCancel(AppointmentCreatEvent event) {
        LocalDateTime cancelTime = LocalDateTime.now().plusHours(48);
        Instant instant = cancelTime.atZone(ZoneId.systemDefault()).toInstant();
        scheduler.schedule(() -> userService.autoCancelAppointment(event.getAppointment().getId()), instant);
    }

    @EventListener
    @Async
    public void onBasketClear(BasketClearEvent event) {
        LocalDateTime clearTime = LocalDateTime.now().plusDays(7);
        Instant instant = clearTime.atZone(ZoneId.systemDefault()).toInstant();
        scheduler.schedule(() -> orderService.basketClear(event.getBasket().getId()), instant);
    }

    @EventListener
    @Async
    public void sendEmailToAllUsers(SendEmailEvent event){
        EmailMessage message = event.getMessage();
        emailService.sendCodeMessage(message.to(), message.subject(), message.message());
    }
}
