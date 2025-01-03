package uz.medsu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.medsu.payload.appointment.AppointmentDTO;
import uz.medsu.payload.appointment.FreeTimeDTO;
import uz.medsu.payload.cards.CardDTO;
import uz.medsu.payload.cards.PaymentDTO;
import uz.medsu.payload.cards.TopUpCardDTO;
import uz.medsu.payload.users.EditPasswordDTO;
import uz.medsu.payload.users.EditUserDTO;
import uz.medsu.payload.users.LocationDTO;
import uz.medsu.payload.users.UserDTO;
import uz.medsu.sevice.UserService;
import uz.medsu.utils.ResponseMessage;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasAuthority('EDIT')")
    @PutMapping("/password")
    public ResponseEntity<ResponseMessage> editPassword(@RequestBody EditPasswordDTO userDTO) {
        return ResponseEntity.ok(userService.editPassword(userDTO));
    }

    @PreAuthorize("hasAuthority('EDIT')")
    @PutMapping("/profile")
    public ResponseEntity<ResponseMessage> updateUser(@RequestBody EditUserDTO userDTO) {
        return ResponseEntity.ok(userService.editUser(userDTO));
    }

    @PreAuthorize("hasAuthority('READ')")
    @GetMapping("/profile")
    public ResponseEntity<ResponseMessage> profile() {
        return ResponseEntity.ok(userService.profile());
    }

    @PreAuthorize("hasAuthority('READ')")
    @GetMapping("/location")
    public ResponseEntity<ResponseMessage> getLocation() {
        return ResponseEntity.ok(userService.getLocation());
    }

    @PreAuthorize("hasAuthority('EDIT')")
    @PutMapping("/location")
    public ResponseEntity<ResponseMessage> setLocation(LocationDTO locationDTO){
        return ResponseEntity.ok(userService.setLocation(locationDTO));
    }

    @PreAuthorize("hasAuthority('READ')")
    @GetMapping("/{id}/appointment")
    public ResponseEntity<ResponseMessage> getAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(userService.showAppointment(id));
    }

    @PreAuthorize("hasAuthority('READ')")
    @GetMapping("/appointment")
    public ResponseEntity<ResponseMessage> getAppointments(Integer page, Integer size) {
        return ResponseEntity.ok(userService.showAppointments(page, size));
    }

    @PreAuthorize("hasAuthority('READ')")
    @GetMapping("/{id}/doctor/free-time")
    public ResponseEntity<ResponseMessage> getAppointments(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getFreeTime(id));
    }

    @PreAuthorize("hasAuthority('READ')")
    @GetMapping("/invoice")
    public ResponseEntity<ResponseMessage> getInvoices(Integer page, Integer size) {
        return ResponseEntity.ok(userService.paymentHistory(page, size));
    }

    @PreAuthorize("hasAuthority('READ')")
    @GetMapping("/{id}/invoice")
    public ResponseEntity<ResponseMessage> getInvoice(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getInvoice(id));
    }

    @PreAuthorize("hasAuthority('POST')")
    @PostMapping("/payment/{id}/appointment")
    public ResponseEntity<ResponseMessage> paymentForAppointment(@RequestBody PaymentDTO paymentDTO, @PathVariable Long id) {
        return ResponseEntity.ok(userService.payToInvoiceForAppointment(id, paymentDTO));
    }

    @PreAuthorize("hasAuthority('POST')")
    @PostMapping("/payment/{id}/order")
    public ResponseEntity<ResponseMessage> paymentForOrder(@RequestBody PaymentDTO paymentDTO, @PathVariable Long id) {
        return ResponseEntity.ok(userService.payToInvoiceForOrder(id, paymentDTO));
    }

    @PreAuthorize("hasAuthority('POST')")
    @PostMapping("/appointment")
    public ResponseEntity<ResponseMessage> addAppointment(@RequestBody AppointmentDTO appointmentDTO) {
        return ResponseEntity.ok(userService.addAppointment(appointmentDTO));
    }

    @PreAuthorize("hasAuthority('EDIT')")
    @PutMapping("/{id}/appointment")
    public ResponseEntity<ResponseMessage> cancelAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(userService.cancelAppointment(id));
    }

    @PreAuthorize("hasAuthority('POST')")
    @PostMapping("/payment")
    public ResponseEntity<ResponseMessage> addPaymentMethod(@RequestBody CardDTO cardDTO) {
        return ResponseEntity.ok(userService.addPaymentMethod(cardDTO));
    }

    @PreAuthorize("hasAuthority('EDIT')")
    @PutMapping("/payment/top-up")
    public ResponseEntity<ResponseMessage> topUpBalance(@RequestBody TopUpCardDTO cardDTO) {
        return ResponseEntity.ok(userService.topUpCard(cardDTO));
    }

    @PreAuthorize("hasAuthority('READ')")
    @GetMapping("/payment")
    public ResponseEntity<ResponseMessage> showPaymentMethod() {
        return ResponseEntity.ok(userService.showPaymentMethod());
    }

    @PreAuthorize("hasAuthority('DELETE')")
    @DeleteMapping("/{id}/payment")
    public ResponseEntity<ResponseMessage> deletePaymentMethod(@PathVariable Long id) {
        return ResponseEntity.ok(userService.deletePaymentMethod(id));
    }

    @PreAuthorize("hasAuthority('READ')")
    @GetMapping("/doctors/top")
    public ResponseEntity<ResponseMessage> getDoctors(Integer page, Integer size) {
        return ResponseEntity.ok(userService.showTopDoctors(page, size));
    }

    @PreAuthorize("hasAuthority('READ')")
    @GetMapping("/doctors/category")
    public ResponseEntity<ResponseMessage> getDoctorsCategory() {
        return ResponseEntity.ok(userService.showDoctorsCategory());
    }

    @PreAuthorize("hasAuthority('READ')")
    @GetMapping("/{id}/doctor")
    public ResponseEntity<ResponseMessage> getDoctor(@PathVariable Long id) {
        return ResponseEntity.ok(userService.showTopDoctor(id));
    }

    @PreAuthorize("hasAuthority('READ')")
    @GetMapping("/doctor/{search}")
    public ResponseEntity<ResponseMessage> getDoctor(@PathVariable String search, Integer page, Integer size) {
        return ResponseEntity.ok(userService.searchDoctors(search, page, size));
    }

    @PreAuthorize("hasAuthority('POST')")
    @PostMapping("/{id}/appointment/{mark}")
    public ResponseEntity<ResponseMessage> addAppointment(@PathVariable Long id, @PathVariable Double mark) {
        return ResponseEntity.ok(userService.evaluateDoctor(id, mark));
    }
}
