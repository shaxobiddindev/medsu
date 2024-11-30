package uz.medsu.sevice.serviceImpl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import uz.medsu.entity.*;
import uz.medsu.enums.OrderStatus;
import uz.medsu.enums.PaymentStatus;
import uz.medsu.enums.Roles;
import uz.medsu.event.OrderCreatedEvent;
import uz.medsu.event.SendEmailEvent;
import uz.medsu.payload.EmailMessage;
import uz.medsu.payload.drugs.ResponseOrderDTO;
import uz.medsu.repository.*;
import uz.medsu.sevice.OrderService;
import uz.medsu.utils.I18nUtil;
import uz.medsu.utils.ResponseMessage;
import uz.medsu.utils.Util;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final BasketDrugRepository basketDrugRepository;
    private final BasketRepository basketRepository;
    private final DrugCloneRepository drugCloneRepository;
    private final UserRepository userRepository;
    private final InvoiceRepository invoiceRepository;
    private final CardRepository cardRepository;
    private final DrugRepository drugRepository;
    private final LocationRepository locationRepository;

    private SendEmailEvent sendEmailEvent;

    @Override
    public ResponseMessage createOrder(Long id) {
        Basket basket = basketRepository.findById(id).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("basketNotFound")));
        if (!basket.getUser().getId().equals(Util.getCurrentUser().getId()))
            throw new RuntimeException(I18nUtil.getMessage("basketNotFound"));
        List<BasketDrug> basketDrugs = basketDrugRepository.findByBasket(basket);

        List<DrugClone> drugClones = parseDrugClone(basketDrugs);

        Location location = locationRepository.findByUser(Util.getCurrentUser()).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("locationNotFound")));

        DrugOrder order = DrugOrder
                .builder()
                .user(basket.getUser())
                .status(OrderStatus.PAYMENT_PENDING)
                .drugs(drugClones)
                .longitude(location.getLongitude())
                .latitude(location.getLatitude())
                .totalPrice(basket.getTotalPrice())
                .build();

        Invoice invoice = Invoice
                .builder()
                .from(basket.getUser())
                .to(userRepository.findByProfession(Roles.ADMIN).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("userNotFound"))))
                .status(PaymentStatus.WAITING)
                .title("Drugs payment")
                .price(basket.getTotalPrice())
                .amount(0.0)
                .build();
        invoiceRepository.save(invoice);
        order.setInvoice(invoice);
        orderRepository.save(order);

        minusDrugQuantity(basketDrugs);

        clearBasket(basket);

        eventPublisher.publishEvent(new OrderCreatedEvent(order));
        eventPublisher.publishEvent(new SendEmailEvent(new EmailMessage(
                Util.getCurrentUser().getEmail(),
                "You have created new order!",
                I18nUtil.getMessage("orderCreated") + "\n\n\n" +
                        "Order ID: " + order.getId() + "\n" +
                        "Total price: " + order.getTotalPrice() + "$\n" +
                        "Order status: " + order.getStatus() + "\n" +
                        "Invoice ID: " + invoice.getId() + "\n" +
                        "Invoice title: " + invoice.getTitle() + "\n" +
                        "Amount paid: " + invoice.getAmount() + "$\n" +
                        "Date: " + order.getUpdatedAt().toLocalDateTime().toLocalDate().toString() + "\n" +
                        "Time: " + order.getUpdatedAt().toLocalDateTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")) + "\n"
        )));
        return ResponseMessage
                .builder()
                .success(true)
                .message(I18nUtil.getMessage("orderCreated"))
                .data(
                        new ResponseOrderDTO(
                                order.getId(),
                                order.getDrugs(),
                                order.getLatitude(),
                                order.getLongitude(),
                                order.getTotalPrice(),
                                order.getInvoice().getId()
                        )
                )
                .build();
    }

    private void minusDrugQuantity(List<BasketDrug> basketDrugs){
        for (BasketDrug basketDrug : basketDrugs) {
            Drug drug = basketDrug.getDrug();
            drug.setQuantity(drug.getQuantity() - basketDrug.getCount());
            drugRepository.save(drug);
        }
    }

    private void plusDrugQuantity(List<DrugClone> drugClones){
        for (DrugClone drugClone : drugClones) {
            Optional<Drug> optionalDrug = drugRepository.findById(drugClone.getDrugId());
            if (optionalDrug.isPresent()){
                Drug drug = optionalDrug.get();
                drug.setQuantity(drug.getQuantity() + drugClone.getQuantity());
                drugRepository.save(drug);
            }
        }
    }

    @Override
    @Transactional
    public ResponseMessage cancelOrder(Long orderId, Boolean autoCancel) {
        DrugOrder order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("orderNotFound")));
        if (order.getStatus().equals(OrderStatus.CANCELLED))
            throw new RuntimeException(I18nUtil.getMessage("orderNotFound"));

        if (autoCancel && !order.getStatus().equals(OrderStatus.PAYMENT_PENDING))
            throw new RuntimeException(I18nUtil.getMessage("orderNotFound"));

        order.setStatus(OrderStatus.CANCELLED);
        Invoice invoice = order.getInvoice();

        if (invoice.getAmount()>0) {
            Card to = cardRepository.findByNumber("9860120105531434").orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("cardNotFound")));
            Card from = cardRepository.findByNumber(invoice.getFromCard()).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("cardNotFound")));
            to.setBalance(to.getBalance()-invoice.getAmount());
            from.setBalance(from.getBalance()+invoice.getAmount());
            cardRepository.save(to);
            cardRepository.save(from);
        }

        plusDrugQuantity(order.getDrugs());

        orderRepository.save(order);

        eventPublisher.publishEvent(new SendEmailEvent(new EmailMessage(
                order.getUser().getEmail(),
                I18nUtil.getMessage("orderCanceled", order.getUser()),
                autoCancel ? I18nUtil.getMessage("orderAutoCancel", order.getUser()) : "Your order has been canceled!" + "\n\n\n" +
                        "Order ID: " + orderId + "\n" +
                        "Total price: " + order.getTotalPrice() + "\n" +
                        "Order status: " + order.getStatus() + "\n" +
                        "Date: " + order.getUpdatedAt().toLocalDateTime().toLocalDate().toString() + "\n" +
                        "Time: " + order.getUpdatedAt().toLocalDateTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")) + "\n"
        )));
        return ResponseMessage
                .builder()
                .success(true)
                .message(I18nUtil.getMessage("orderCanceled"))
                .build();
    }

    @Override
    @Transactional
    public void basketClear(Long id) {
        Basket basket = basketRepository.findById(id).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("basketNotFound")));
        clearBasket(basket);
        eventPublisher.publishEvent(new SendEmailEvent(new EmailMessage(
                basket.getUser().getEmail(),
                "Basket cleared!",
                "Your basket cleared at: " + LocalDateTime.now().toLocalDate().toString() + "  " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
        )));
    }

    @Override
    public ResponseMessage getOrders(Integer page, Integer size) {
        List<ResponseOrderDTO> orderDTOS = orderRepository.findByUser(Util.getCurrentUser()).stream().map(drugOrder -> {
            return new ResponseOrderDTO(
                    drugOrder.getId(),
                    drugOrder.getDrugs(),
                    drugOrder.getLatitude(),
                    drugOrder.getLongitude(),
                    drugOrder.getTotalPrice(),
                    drugOrder.getInvoice().getId()
            );
        }).toList();
        return ResponseMessage.builder().success(true).data(orderDTOS).build();
    }

    @Override
    public ResponseMessage getOrder(Long id) {
        DrugOrder order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("orderNotFound")));
        if (!order.getUser().getId().equals(Util.getCurrentUser().getId())) throw new RuntimeException(I18nUtil.getMessage("orderNotFound"));
        return ResponseMessage.builder().success(true).data(
                new ResponseOrderDTO(
                        order.getId(),
                        order.getDrugs(),
                        order.getLatitude(),
                        order.getLongitude(),
                        order.getTotalPrice(),
                        order.getInvoice().getId()
                )
        ).build();
    }

    private void clearBasket(Basket basket) {
        basketDrugRepository.deleteByBasket(basket.getId());
        basketRepository.delete(basket);
    }

    private List<DrugClone> parseDrugClone(List<BasketDrug> basketDrugs) {
        return basketDrugs.stream().map(drug -> {
            DrugClone drugClone = DrugClone
                    .builder()
                    .description(drug.getDrug().getDescription())
                    .name(drug.getDrug().getName())
                    .price(drug.getDrug().getPrice())
                    .imageUrl(drug.getDrug().getImageUrl())
                    .totalPrice(drug.getCount() * drug.getDrug().getPrice())
                    .quantity(drug.getCount())
                    .drugId(drug.getDrug().getId())
                    .build();
            return drugCloneRepository.save(drugClone);
        }).toList();
    }
}
