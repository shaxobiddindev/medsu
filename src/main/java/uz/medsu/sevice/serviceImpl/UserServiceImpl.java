package uz.medsu.sevice.serviceImpl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.medsu.entity.*;
import uz.medsu.enums.*;
import uz.medsu.event.AppointmentCreatEvent;
import uz.medsu.event.SendEmailEvent;
import uz.medsu.payload.EmailMessage;
import uz.medsu.payload.appointment.AppointmentDTO;
import uz.medsu.payload.appointment.DateDTO;
import uz.medsu.payload.appointment.FreeTimeDTO;
import uz.medsu.payload.appointment.ResponseAppointmentDTO;
import uz.medsu.payload.cards.*;
import uz.medsu.payload.doctors.ResponseDoctorDTO;
import uz.medsu.payload.users.*;
import uz.medsu.repository.*;
import uz.medsu.sevice.UserService;
import uz.medsu.utils.I18nUtil;
import uz.medsu.utils.ResponseMessage;
import uz.medsu.utils.Util;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppointmentRepository appointmentRepository;
    private final CardRepository cardRepository;
    private final InvoiceRepository invoiceRepository;
    private final RatingRepository ratingRepository;
    private final SpecialityRepository doctorRepository;
    private final OrderRepository orderRepository;
    private final LocationRepository locationRepository;
    private final ApplicationEventPublisher eventPublisher;
    @Value("${my_var.start-time}")
    private String startTime;
    @Value("${my_var.break-time}")
    private String breakTime;
    @Value("${my_var.end-time}")
    private String endTime;


    @Override
    public ResponseMessage editPassword(EditPasswordDTO editPasswordDTO) {
        User user = Util.getCurrentUser();
        if (!passwordEncoder.matches(editPasswordDTO.oldPassword(), user.getPassword()))
            throw new RuntimeException(I18nUtil.getMessage("passwordNotMatch"));
        user.setPassword(passwordEncoder.encode(editPasswordDTO.newPassword()));
        userRepository.save(user);
        return ResponseMessage
                .builder()
                .success(true)
                .message(I18nUtil.getMessage("passwordChanged"))
                .data(new ReturnUserDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail(), user.getAge(), user.getGender().toString(), user.getRole(), user.getEnabled(), user.getIsNonLocked(), user.getImageUrl()))
                .build();
    }

    @Override
    public ResponseMessage editUser(EditUserDTO userDTO) {
        User user = Util.getCurrentUser();
        user.setAge(userDTO.age() != 0 ? userDTO.age() : user.getAge());
        user.setFirstName(userDTO.firstName().isBlank() ? user.getFirstName() : userDTO.firstName());
        user.setLastName(userDTO.lastName().isBlank() ? user.getLastName() : userDTO.lastName());
        user.setGender(userDTO.gender().isBlank() ? user.getGender() : Gender.valueOf(userDTO.gender().toUpperCase()));

        userRepository.save(user);
        return ResponseMessage
                .builder()
                .success(true)
                .message(I18nUtil.getMessage("userChangedSuccess"))
                .data(new ReturnUserDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail(), user.getAge(), user.getGender().toString(), user.getRole(), user.getEnabled(), user.getIsNonLocked(), user.getImageUrl()))
                .build();
    }

    @Override
    public ResponseMessage showAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("appointmentNotFound")));
        if (!appointment.getUser().getId().equals(Util.getCurrentUser().getId()))
            throw new RuntimeException(I18nUtil.getMessage("appointmentNotFound"));

        Optional<Location> byUser = locationRepository.findByUser(appointment.getDoctor().getUser());

        ResponseDoctorDTO responseDoctorDTO = new ResponseDoctorDTO(
                appointment.getDoctor().getId(),
                appointment.getDoctor().getAbout(),
                appointment.getDoctor().getUser().getFirstName(),
                appointment.getDoctor().getUser().getLastName(),
                appointment.getDoctor().getDoctorSpecialty().toString(),
                appointment.getDoctor().getAppointmentPrice(),
                appointment.getDoctor().getRating(),
                byUser.map(Location::getLatitude).orElse(null),
                byUser.map(Location::getLongitude).orElse(null),
                appointment.getDoctor().getUser().getImageUrl()
        );
        return ResponseMessage
                .builder()
                .success(true)
                .data(new ResponseAppointmentDTO(
                        appointment.getId(),
                        appointment.getUser().getId(),
                        responseDoctorDTO,
                        appointment.getDate().toLocalDateTime().toLocalDate().toString(),
                        appointment.getTime(),
                        appointment.getStatus().toString(),
                        appointment.getInvoice().getId()
                ))
                .build();
    }

    @Override
    public ResponseMessage showAppointments(Integer page, Integer size) {
        List<ResponseAppointmentDTO> appointmentDTOS = appointmentRepository.findAllByUser(Util.getCurrentUser(), PageRequest.of(page, size)).toList().stream().map(appointment -> {
            ResponseDoctorDTO responseDoctorDTO = new ResponseDoctorDTO(
                    appointment.getDoctor().getId(),
                    appointment.getDoctor().getAbout(),
                    appointment.getDoctor().getUser().getFirstName(),
                    appointment.getDoctor().getUser().getLastName(),
                    appointment.getDoctor().getDoctorSpecialty().toString(),
                    appointment.getDoctor().getAppointmentPrice(),
                    appointment.getDoctor().getRating(),
                    null,
                    null,
                    appointment.getDoctor().getUser().getImageUrl()
            );
            return new ResponseAppointmentDTO(
                    appointment.getId(),
                    appointment.getUser().getId(),
                    responseDoctorDTO,
                    appointment.getDate().toLocalDateTime().toLocalDate().toString(),
                    appointment.getTime(),
                    appointment.getStatus().toString(),
                    appointment.getInvoice().getId()
            );
        }).toList();
        return ResponseMessage
                .builder()
                .success(true)
                .data(appointmentDTOS)
                .build();
    }

    @Override
    public ResponseMessage paymentHistory(Integer page, Integer size) {
        List<ResponseInvoiceDTO> invoiceDTOS = invoiceRepository.findAllByFrom(Util.getCurrentUser(), PageRequest.of(page, size)).toList().stream().map(invoice -> {
            return new ResponseInvoiceDTO(
                    invoice.getId(),
                    invoice.getTitle(),
                    invoice.getDescription(),
                    invoice.getTo().getId(),
                    invoice.getFrom().getId(),
                    invoice.getPrice(),
                    invoice.getAmount(),
                    invoice.getStatus().toString(),
                    invoice.getCreatedAt().toLocalDateTime().toString(),
                    invoice.getUpdatedAt().toLocalDateTime().toString()
            );
        }).toList();

        return ResponseMessage.builder()
                .success(true)
                .data(invoiceDTOS)
                .build();
    }

    @Override
    public ResponseMessage addPaymentMethod(CardDTO cardDTO) {
        if (cardDTO.cardNumber().length() != 16) throw new RuntimeException(I18nUtil.getMessage("invalidCardNumber"));
        Card card = Card
                .builder()
                .user(Util.getCurrentUser())
                .balance(cardDTO.balance() == null || cardDTO.balance() < 0 ? 0 : cardDTO.balance())
                .number(checkCardNumber(cardDTO.cardNumber()).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("cardNumberFormatError"))))
                .expiryDate(cardDTO.expireDate())
                .build();

        if (card.getNumber().startsWith("9860")) {
            card.setType("HUMO");
        } else if (card.getNumber().startsWith("8600") || card.getNumber().startsWith("5614")) {
            card.setType("UZCARD");
        } else if (card.getNumber().startsWith("5")) {
            card.setType("MASTERCARD");
        } else if (card.getNumber().startsWith("4")) {
            card.setType("VISA");
        } else {
            throw new RuntimeException(I18nUtil.getMessage("invalidCardNumber"));
        }

        String[] split = cardDTO.expireDate().split("/");
        if (LocalDate.now().getYear() < Integer.parseInt(split[1]) ||
                LocalDate.now().getYear() == Integer.parseInt(split[1]) && LocalDate.now().getMonth().getValue() < Integer.parseInt(split[0])) {
            throw new RuntimeException(I18nUtil.getMessage("invalidExpireDate"));
        }

        if (cardRepository.findByNumber(card.getNumber()).isPresent())
            throw new RuntimeException(I18nUtil.getMessage("cardAlreadyExist"));

        cardRepository.save(card);


        return ResponseMessage
                .builder()
                .success(true)
                .data(new ResponseCardDTO(card.getId(), cardDTO.cardNumber(), card.getExpiryDate(), card.getBalance()))
                .build();
    }

    @Override
    public ResponseMessage showPaymentMethod() {
        List<ResponseCardDTO> cardDTOS = cardRepository.findByUser(Util.getCurrentUser()).stream().map(card -> {
            return new ResponseCardDTO(card.getId(), card.getNumber(), card.getExpiryDate(), card.getBalance());
        }).toList();

        return ResponseMessage.builder().success(true).data(cardDTOS).build();
    }

    @Override
    public ResponseMessage payToInvoiceForOrder(Long orderId, PaymentDTO paymentDTO) {
        Card card = cardRepository.findById(paymentDTO.cardId()).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("cardNotFound")));
        DrugOrder order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("orderNotFound")));
        Invoice invoice = order.getInvoice();
        if (card.getBalance() < invoice.getPrice()) throw new RuntimeException(I18nUtil.getMessage("invalidBalance"));
        invoice.setAmount(invoice.getAmount() + invoice.getPrice());
        Card adminCard = cardRepository.findByNumber(invoice.getToCard()).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("cardNotFound")));
        adminCard.setBalance(adminCard.getBalance() + invoice.getPrice());
        card.setBalance(card.getBalance() - invoice.getPrice());
        invoice.setStatus(PaymentStatus.SUCCESS);
        order.setStatus(OrderStatus.APPROVED);
        invoice.setFromCard(card.getNumber());
        cardRepository.save(card);
        cardRepository.save(adminCard);
        invoiceRepository.save(invoice);
        orderRepository.save(order);

        eventPublisher.publishEvent(new SendEmailEvent(new EmailMessage(
                order.getUser().getEmail(),
                I18nUtil.getMessage("paymentSuccess", order.getUser()),
                "Your order successfully approved!" + "\n\n\n" +
                        "Order ID: " + order.getId() + "\n" +
                        "Total price: " + order.getTotalPrice() + "$\n" +
                        "Order status: " + order.getStatus() + "\n" +
                        "Invoice ID: " + invoice.getId() + "\n" +
                        "Invoice title: " + invoice.getTitle() + "\n" +
                        "Amount paid: " + invoice.getAmount() + "$\n" +
                        "Date: " + order.getUpdatedAt().toLocalDateTime().toLocalDate().toString() + "\n" +
                        "Time: " + order.getUpdatedAt().toLocalDateTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")) + "\n"

        )));
        return ResponseMessage.builder()
                .success(true)
                .message(I18nUtil.getMessage("paymentSuccess"))
                .data(new ResponseInvoiceDTO(invoice.getId(), invoice.getTitle(), invoice.getDescription(), invoice.getTo().getId(), invoice.getFrom().getId(), invoice.getPrice(), invoice.getAmount(), invoice.getStatus().toString(), invoice.getCreatedAt().toLocalDateTime().toString(), invoice.getUpdatedAt().toLocalDateTime().toString()))
                .build();
    }

    @Override
    public ResponseMessage topUpCard(TopUpCardDTO cardDTO) {
        Card card = cardRepository.findByNumber(cardDTO.cardNumber()).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("cardNotFound")));
        card.setBalance(card.getBalance() + cardDTO.amount() > 0 ? cardDTO.amount() : 0);
        return ResponseMessage
                .builder()
                .success(true)
                .message("Your balance has been topped up!").build();
    }

    @Override
    public ResponseMessage getInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("invoiceNotFound")));
        if (!invoice.getFrom().getId().equals(Util.getCurrentUser().getId()))
            throw new RuntimeException(I18nUtil.getMessage("invoiceNotFound"));
        return ResponseMessage
                .builder()
                .success(true)
                .data(
                        new ResponseInvoiceDTO(
                                invoice.getId(),
                                invoice.getTitle(),
                                invoice.getDescription(),
                                invoice.getTo().getId(),
                                invoice.getFrom().getId(),
                                invoice.getPrice(),
                                invoice.getAmount(),
                                invoice.getStatus().toString(),
                                invoice.getCreatedAt().toLocalDateTime().toString(),
                                invoice.getUpdatedAt().toLocalDateTime().toString()
                        )
                ).build();
    }

    @Override
    public ResponseMessage setLocation(LocationDTO locationDTO) {
        Optional<Location> optionalLocation = locationRepository.findByUser(Util.getCurrentUser());
        Location location;
        location = optionalLocation
                .orElseGet(() ->
                        Location
                                .builder()
                                .user(Util.getCurrentUser())
                                .build()
                );
        location.setLongitude(locationDTO.longitude());
        location.setLatitude(locationDTO.latitude());
        locationRepository.save(location);
        return ResponseMessage
                .builder()
                .success(true)
                .message(I18nUtil.getMessage("locationUpdated"))
                .build();
    }

    @Override
    public ResponseMessage getLocation() {
        Location location = locationRepository.findByUser(Util.getCurrentUser()).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("locationNotFound")));
        return ResponseMessage
                .builder()
                .success(true)
                .data(
                        new LocationDTO(
                                location.getLatitude(),
                                location.getLongitude()
                        )
                )
                .build();
    }

    @Override
    public ResponseMessage profile() {
        User currentUser = Util.getCurrentUser();
        return ResponseMessage
                .builder()
                .success(true)
                .data(
                        new ReturnUserDTO(
                                currentUser.getId(),
                                currentUser.getFirstName(),
                                currentUser.getLastName(),
                                currentUser.getUsername(),
                                currentUser.getEmail(),
                                currentUser.getAge(),
                                currentUser.getGender().toString(),
                                currentUser.getRole(),
                                currentUser.getEnabled(),
                                currentUser.getIsNonLocked(),
                                currentUser.getImageUrl()
                        )
                )
                .build();
    }

    @Override
    public ResponseMessage getFreeTime(Long id) {
        Doctor doctor = doctorRepository.findById(id).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("doctorNotFound")));
        Map<DateDTO, List<FreeTimeDTO>> freeTimes = getFreeTimeInDate(doctor, LocalDate.now());
        return ResponseMessage.builder().success(true).data(freeTimes).build();
    }

    @Override
    public ResponseMessage showTopDoctor(Long id) {
        Doctor doctor = doctorRepository.findById(id).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("doctorNotFound")));
        Optional<Location> byUser = locationRepository.findByUser(doctor.getUser());
        return ResponseMessage.builder()
                .success(true)
                .data(
                        new ResponseDoctorDTO(
                                doctor.getId(),
                                doctor.getAbout(),
                                doctor.getUser().getFirstName(),
                                doctor.getUser().getLastName(),
                                doctor.getDoctorSpecialty().toString(),
                                doctor.getAppointmentPrice(),
                                doctor.getRating(),
                                byUser.map(Location::getLatitude).orElse(null),
                                byUser.map(Location::getLongitude).orElse(null),
                                doctor.getUser().getImageUrl()
                        )
                )
                .build();
    }

    @Override
    public ResponseMessage showDoctorsCategory() {
        return ResponseMessage.builder().success(true).data(
                Arrays.stream(DoctorSpeciality.values()).toList()
        ).build();
    }

    private Map<DateDTO, List<FreeTimeDTO>> getFreeTimeInDate(Doctor doctor, LocalDate now) {
        Map<DateDTO, List<FreeTimeDTO>> result = new HashMap<>();
        for (int i = 0; i < 30; i++) {
            LocalDate date = now.plusDays(i);
            List<Appointment> appointments = appointmentRepository.findAllByDoctorAndDate(doctor, Timestamp.valueOf(LocalDateTime.of(date, LocalTime.of(0, 0))));
            List<FreeTimeDTO> freeTimes = allTimes();
            for (FreeTimeDTO freeTime : freeTimes) {
                for (Appointment appointment : appointments) {
                    if (freeTime.getTime().equals(appointment.getTime())) {
                        freeTime.setIsBooked(true);
                    }
                }
            }
            result.put(new DateDTO(date, date.getDayOfWeek().toString()), freeTimes);
        }

        return result;
    }

    private List<FreeTimeDTO> allTimes() {
        List<FreeTimeDTO> freeTimes = new ArrayList<>();
        freeTimes.add(new FreeTimeDTO(startTime, false));
        for (int i = 10; i < 19; i++) {
            if (breakTime.equals((i + ":00"))) continue;
            freeTimes.add(new FreeTimeDTO(i + ":00", false));
        }
        return freeTimes;
    }

    @Override
    public ResponseMessage payToInvoiceForAppointment(Long id, PaymentDTO paymentDTO) {
        Card card = cardRepository.findById(paymentDTO.cardId()).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("cardNotFound")));
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("appointmentNotFound")));
        Invoice invoice = appointment.getInvoice();
        if (card.getBalance() < invoice.getPrice()) throw new RuntimeException(I18nUtil.getMessage("invalidBalance"));
        invoice.setAmount(invoice.getAmount() + invoice.getPrice());
        Card adminCard = cardRepository.findByNumber(invoice.getToCard()).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("cardNotFound")));
        adminCard.setBalance(adminCard.getBalance() + invoice.getPrice());
        card.setBalance(card.getBalance() - invoice.getPrice());
        invoice.setStatus(PaymentStatus.SUCCESS);
        appointment.setStatus(AppointmentStatus.APPROVED);
        invoice.setFromCard(card.getNumber());
        cardRepository.save(card);
        cardRepository.save(adminCard);
        invoiceRepository.save(invoice);
        appointmentRepository.save(appointment);

        eventPublisher.publishEvent(new SendEmailEvent(new EmailMessage(
                appointment.getUser().getEmail(),
                I18nUtil.getMessage("paymentSuccess", appointment.getUser()),
                "Your appointment successfully approved!" + "\n\n\n" +
                        "Appointment ID: " + appointment.getId() + "\n" +
                        "Total price: " + appointment.getDoctor().getAppointmentPrice() + "$\n" +
                        "Appointment status: " + appointment.getStatus() + "\n" +
                        "Invoice ID: " + invoice.getId() + "\n" +
                        "Invoice title: " + invoice.getTitle() + "\n" +
                        "Amount paid: " + invoice.getAmount() + "$\n" +
                        "Date: " + appointment.getUpdatedAt().toLocalDateTime().toLocalDate().toString() + "\n" +
                        "Time: " + appointment.getUpdatedAt().toLocalDateTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")) + "\n"

        )));

        return ResponseMessage.builder()
                .success(true)
                .message(I18nUtil.getMessage("paymentSuccess"))
                .data(new ResponseInvoiceDTO(invoice.getId(), invoice.getTitle(), invoice.getDescription(), invoice.getTo().getId(), invoice.getFrom().getId(), invoice.getPrice(), invoice.getAmount(), invoice.getStatus().toString(), invoice.getCreatedAt().toLocalDateTime().toString(), invoice.getUpdatedAt().toLocalDateTime().toString()))
                .build();
    }

    @Override
    public ResponseMessage showTopDoctors(Integer page, Integer size) {
        List<ResponseDoctorDTO> doctorDTOS = doctorRepository.findAllByOrderByRatingDesc(PageRequest.of(page, size)).toList().stream().map(doctor -> {
            Optional<Location> byUser = locationRepository.findByUser(doctor.getUser());
            return new ResponseDoctorDTO(
                    doctor.getId(),
                    doctor.getAbout(),
                    doctor.getUser().getFirstName(),
                    doctor.getUser().getLastName(),
                    doctor.getDoctorSpecialty().toString(),
                    doctor.getAppointmentPrice(),
                    doctor.getRating(),
                    byUser.map(Location::getLatitude).orElse(null),
                    byUser.map(Location::getLongitude).orElse(null),
                    doctor.getUser().getImageUrl()
            );
        }).toList();

        return ResponseMessage.builder()
                .success(true)
                .data(doctorDTOS)
                .build();
    }

    @Override
    public ResponseMessage searchDoctors(String text, Integer page, Integer size) {
        List<ResponseDoctorDTO> doctorDTOS = doctorRepository.searchDoctorsByUserFirstNameOrLastNameOrSpecialty(text, PageRequest.of(page, size)).toList().stream().map(doctor -> {
            Optional<Location> byUser = locationRepository.findByUser(doctor.getUser());
            return new ResponseDoctorDTO(
                    doctor.getId(),
                    doctor.getAbout(),
                    doctor.getUser().getFirstName(),
                    doctor.getUser().getLastName(),
                    doctor.getDoctorSpecialty().toString(),
                    doctor.getAppointmentPrice(),
                    doctor.getRating(),
                    byUser.map(Location::getLatitude).orElse(null),
                    byUser.map(Location::getLongitude).orElse(null),
                    doctor.getUser().getImageUrl()
            );
        }).toList();

        return ResponseMessage.builder()
                .success(true)
                .data(doctorDTOS)
                .build();
    }

    @Override
    public ResponseMessage evaluateDoctor(Long appointmentId, Double mark) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("appointmentNotFound")));
        if (!appointment.getStatus().equals(AppointmentStatus.COMPLETED))
            throw new RuntimeException(I18nUtil.getMessage("appointmentNotCompleted"));
        if (!appointment.getUser().getId().equals(Util.getCurrentUser().getId()))
            throw new RuntimeException(I18nUtil.getMessage("appointmentNotFound"));
        Rating rating = Rating.builder()
                .userId(Util.getCurrentUser().getId())
                .doctorId(appointment.getDoctor().getId())
                .rating(mark)
                .appointmentId(appointmentId)
                .build();
        ratingRepository.save(rating);
        Doctor doctor = doctorRepository.findById(appointment.getDoctor().getId()).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("doctorNotFound")));
        doctor.setRating(ratingRepository.sumRatingByDoctorId(doctor.getId()));
        doctorRepository.save(doctor);
        return ResponseMessage.builder()
                .success(true)
                .message(I18nUtil.getMessage("appointmentRateSuccess"))
                .data(rating)
                .build();
    }


    @Override
    public ResponseMessage addAppointment(AppointmentDTO appointmentDTO) {
        Timestamp date = checkDate(appointmentDTO.date()).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("dateFormatError")));
        if (date.toLocalDateTime().isBefore(LocalDateTime.now()))
            throw new RuntimeException("Only upcoming dates can be booked!");
        Appointment appointment = Appointment
                .builder()
                .user(Util.getCurrentUser())
                .doctor(doctorRepository.findById(appointmentDTO.doctorId()).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("doctorNotFound"))))
                .date(checkDate(appointmentDTO.date()).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("dateFormatError"))))
                .time(formatTimes(appointmentDTO.time()).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("timeFormatError"))))
                .status(AppointmentStatus.UNPAID)
                .build();

        List<Appointment> byDateAndTimes = appointmentRepository.findAllByDateAndTime(appointment.getDate(), appointment.getTime()).stream().filter(cons ->
                cons.getStatus().equals(AppointmentStatus.APPROVED) || cons.getStatus().equals(AppointmentStatus.UNPAID)
        ).toList();

        if (!byDateAndTimes.isEmpty())
            throw new RuntimeException(I18nUtil.getMessage("dateTimeExists"));

        Invoice invoice = Invoice
                .builder()
                .price(appointment.getDoctor().getAppointmentPrice())
                .amount(0.0)
                .title("Appointment payment")
                .status(PaymentStatus.WAITING)
                .to(userRepository.findByProfession(Roles.ADMIN).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("userNotFound"))))
                .toCard("9860120105531434")
                .from(appointment.getUser())
                .build();
        invoiceRepository.save(invoice);
        appointment.setInvoice(invoice);
        appointmentRepository.save(appointment);

        eventPublisher.publishEvent(new AppointmentCreatEvent(appointment));
        eventPublisher.publishEvent(new SendEmailEvent(new EmailMessage(
                Util.getCurrentUser().getEmail(),
                "You have created new appointment!",
                I18nUtil.getMessage("appointmentCreated") + "\n\n\n" +
                        "Appointment ID: " + appointment.getId() + "\n" +
                        "Appointment status: " + appointment.getStatus() + "\n" +
                        "Total price: " + appointment.getDoctor().getAppointmentPrice() + "$\n" +
                        "Invoice ID: " + invoice.getId() + "\n" +
                        "Invoice title: " + invoice.getTitle() + "\n" +
                        "Amount paid: " + invoice.getAmount() + "$\n" +
                        "Date: " + appointment.getUpdatedAt().toLocalDateTime().toLocalDate().toString() + "\n" +
                        "Time: " + appointment.getUpdatedAt().toLocalDateTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")) + "\n"
        )));
        Optional<Location> byUser = locationRepository.findByUser(appointment.getDoctor().getUser());
        ResponseDoctorDTO responseDoctorDTO = new ResponseDoctorDTO(
                appointment.getDoctor().getId(),
                appointment.getDoctor().getAbout(),
                appointment.getDoctor().getUser().getFirstName(),
                appointment.getDoctor().getUser().getLastName(),
                appointment.getDoctor().getDoctorSpecialty().toString(),
                appointment.getDoctor().getAppointmentPrice(),
                appointment.getDoctor().getRating(),
                byUser.map(Location::getLatitude).orElse(null),
                byUser.map(Location::getLongitude).orElse(null),
                appointment.getDoctor().getUser().getImageUrl()
        );
        return ResponseMessage
                .builder()
                .success(true)
                .message(I18nUtil.getMessage("appointmentCreated"))
                .data(new ResponseAppointmentDTO(appointment.getId(), appointment.getUser().getId(), responseDoctorDTO, appointment.getDate().toLocalDateTime().toLocalDate().toString(), appointment.getTime(), appointment.getStatus().toString(), appointment.getInvoice().getId()))
                .build();
    }

    @Override
    public ResponseMessage cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("appointmentNotFound")));
        if (appointment.getUser().getId().equals(Util.getCurrentUser().getId()))
            throw new RuntimeException(I18nUtil.getMessage("setStatusError"));
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
        Invoice invoice = appointment.getInvoice();
        invoice.setStatus(PaymentStatus.CANCELLED);

        if (invoice.getAmount() > 0) {
            Card to = cardRepository.findByNumber("9860120105531434").orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("cardNotFound")));
            Card from = cardRepository.findByNumber(invoice.getFromCard()).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("cardNotFound")));
            to.setBalance(to.getBalance() - invoice.getAmount());
            from.setBalance(from.getBalance() + invoice.getAmount());
            cardRepository.save(to);
            cardRepository.save(from);
        }

        invoiceRepository.save(invoice);

        eventPublisher.publishEvent(new SendEmailEvent(new EmailMessage(
                Util.getCurrentUser().getEmail(),
                "You have canceled appointment!",
                I18nUtil.getMessage("appointmentCancel") + "\n\n\n" +
                        "Appointment ID: " + appointment.getId() + "\n" +
                        "Appointment status: " + appointment.getStatus() + "\n" +
                        "Total price: " + appointment.getDoctor().getAppointmentPrice() + "$\n" +
                        "Date: " + appointment.getUpdatedAt().toLocalDateTime().toLocalDate().toString() + "\n" +
                        "Time: " + appointment.getUpdatedAt().toLocalDateTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")) + "\n"
        )));
        return ResponseMessage
                .builder()
                .success(true)
                .message(I18nUtil.getMessage("appointmentCancel"))
                .build();
    }

    @Override
    @Transactional
    public ResponseMessage autoCancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("appointmentNotFound")));
        if (appointment.getStatus().equals(AppointmentStatus.APPROVED))
            throw new RuntimeException("Appointment approved, cannot cancel!");
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
        Invoice invoice = appointment.getInvoice();
        invoice.setStatus(PaymentStatus.CANCELLED);
        if (invoice.getAmount() > 0) {
            Card to = cardRepository.findByNumber("9860120105531434").orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("cardNotFound")));
            Card from = cardRepository.findByNumber(invoice.getFromCard()).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("cardNotFound")));
            to.setBalance(to.getBalance() - invoice.getAmount());
            from.setBalance(from.getBalance() + invoice.getAmount());
            cardRepository.save(to);
            cardRepository.save(from);
        }
        invoiceRepository.save(invoice);

        eventPublisher.publishEvent(new SendEmailEvent(new EmailMessage(
                appointment.getUser().getEmail(),
                "You have canceled appointment!",
                I18nUtil.getMessage("appointmentAutoCancel", appointment.getUser()) + "\n\n\n" +
                        "Appointment ID: " + appointment.getId() + "\n" +
                        "Appointment status: " + appointment.getStatus() + "\n" +
                        "Amount paid: " + invoice.getAmount() + "$\n" +
                        "Date: " + appointment.getUpdatedAt().toLocalDateTime().toLocalDate().toString() + "\n" +
                        "Time: " + appointment.getUpdatedAt().toLocalDateTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")) + "\n"
        )));
        ResponseDoctorDTO responseDoctorDTO = new ResponseDoctorDTO(
                appointment.getDoctor().getId(),
                appointment.getDoctor().getAbout(),
                appointment.getDoctor().getUser().getFirstName(),
                appointment.getDoctor().getUser().getLastName(),
                appointment.getDoctor().getDoctorSpecialty().toString(),
                appointment.getDoctor().getAppointmentPrice(),
                appointment.getDoctor().getRating(),
                null,
                null,
                appointment.getDoctor().getUser().getImageUrl()
        );
        return ResponseMessage
                .builder()
                .success(true)
                .data(new ResponseAppointmentDTO(appointment.getId(), appointment.getUser().getId(), responseDoctorDTO, appointment.getDate().toLocalDateTime().toLocalDate().toString(), appointment.getTime(), appointment.getStatus().toString(), invoice.getId()))
                .build();
    }

    @Override
    public ResponseMessage deletePaymentMethod(Long id) {
        cardRepository.deleteById(id);
        return ResponseMessage.builder().success(true).message(I18nUtil.getMessage("cardDeleted")).build();
    }


    private Optional<Timestamp> checkDate(String date) {
        try {
            LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return !DayOfWeek.SUNDAY.equals(localDate.getDayOfWeek()) ? Optional.of(Timestamp.valueOf(LocalDateTime.of(localDate, LocalTime.of(0, 0)))) : Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException(I18nUtil.getMessage("dateFormatError"));
        }
    }

    private Optional<String> formatTimes(String time) {
        try {
            if (!time.split(":")[1].equals("00")) throw new RuntimeException(I18nUtil.getMessage("timeFormatError"));
            int start = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm")).getHour();
            int breakHour = LocalTime.parse(breakTime, DateTimeFormatter.ofPattern("HH:mm")).getHour();
            int end = LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HH:mm")).getHour();
            int inputTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm")).getHour();
            return (start <= inputTime && end > inputTime && breakHour != inputTime) ? Optional.of(time) : Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException(I18nUtil.getMessage("timeFormatError"));
        }
    }

    private Optional<String> checkCardNumber(String number) {
        for (char c : number.toCharArray()) {
            Integer.parseInt(String.valueOf(c));
        }
        return Optional.of(number);
    }
}
