package uz.medsu.sevice;


import uz.medsu.entity.Doctor;
import uz.medsu.payload.appointment.AppointmentDTO;
import uz.medsu.payload.cards.CardDTO;
import uz.medsu.payload.cards.PaymentDTO;
import uz.medsu.payload.users.EditPasswordDTO;
import uz.medsu.payload.users.EditUserDTO;
import uz.medsu.payload.users.UserDTO;
import uz.medsu.utils.ResponseMessage;

public interface UserService {
    ResponseMessage editPassword(EditPasswordDTO editPasswordDTO);
    ResponseMessage editUser(EditUserDTO userDTO);
    ResponseMessage showAppointment(Long id);
    ResponseMessage showAppointments(Integer page, Integer size);
    ResponseMessage paymentHistory(Integer page, Integer size);
    ResponseMessage addPaymentMethod(CardDTO cardDTO);
    ResponseMessage showPaymentMethod();
    ResponseMessage payToInvoiceForAppointment(Long id, PaymentDTO paymentDTO);
    ResponseMessage showTopDoctors(Integer page, Integer size);
    ResponseMessage searchDoctors(String text, Integer page, Integer size);
    ResponseMessage evaluateDoctor(Long appointmentId, Double mark);
    ResponseMessage addAppointment(AppointmentDTO appointment);
    ResponseMessage cancelAppointment(Long appointmentId);
    ResponseMessage autoCancelAppointment(Long appointmentId);
    ResponseMessage deletePaymentMethod(Long id);
    ResponseMessage payToInvoiceForOrder(Long orderId, PaymentDTO paymentDTO);
}
