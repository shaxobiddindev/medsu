package uz.medsu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.medsu.payload.appointment.AppointmentDTO;
import uz.medsu.payload.cards.CardDTO;
import uz.medsu.payload.cards.PaymentDTO;
import uz.medsu.payload.users.EditPasswordDTO;
import uz.medsu.payload.users.EditUserDTO;
import uz.medsu.payload.users.UserDTO;
import uz.medsu.sevice.UserService;
import uz.medsu.utils.ResponseMessage;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@PreAuthorize(value = "hasRole('USER')")
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasAuthority('EDIT')")
    @PutMapping("/password")
    public ResponseEntity<ResponseMessage> editPassword(@RequestBody EditPasswordDTO userDTO) {
        return ResponseEntity.ok(userService.editPassword(userDTO));
    }

    @PreAuthorize("hasAuthority('EDIT')")
    @PutMapping
    public ResponseEntity<ResponseMessage> updateUser(@RequestBody EditUserDTO userDTO) {
        return ResponseEntity.ok(userService.editUser(userDTO));
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
    @GetMapping("/invoince")
    public ResponseEntity<ResponseMessage> getInvoice(Integer page, Integer size) {
        return ResponseEntity.ok(userService.paymentHistory(page, size));
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
    @GetMapping("/doctor")
    public ResponseEntity<ResponseMessage> getDoctors(Integer page, Integer size) {
        return ResponseEntity.ok(userService.showTopDoctors(page, size));
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
