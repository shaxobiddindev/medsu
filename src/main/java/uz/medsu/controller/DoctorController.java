package uz.medsu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.medsu.entity.Appointment;
import uz.medsu.payload.users.EditPasswordDTO;
import uz.medsu.payload.users.EditUserDTO;
import uz.medsu.sevice.DoctorService;
import uz.medsu.sevice.UserService;
import uz.medsu.utils.ResponseMessage;

@RestController
@RequiredArgsConstructor
@RequestMapping("/doctor")
public class DoctorController {
    private final DoctorService doctorService;
    private final UserService userService;

    @PreAuthorize("{hasAnyRole('DOCTOR', 'ADMIN'), hasAuthority('READ')}")
    @GetMapping("/appointment")
    public ResponseEntity<ResponseMessage> getAppointments(Integer page, Integer size) {
        return ResponseEntity.ok(doctorService.showAppointments(page, size));
    }

    @PreAuthorize("{hasAnyRole('DOCTOR', 'ADMIN'), hasAuthority('READ')}")
    @GetMapping("/{id}/appointment")
    public ResponseEntity<ResponseMessage> getAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.showAppointments(id));
    }

    @PreAuthorize("{hasAnyRole('DOCTOR', 'ADMIN'), hasAuthority('EDIT')}")
    @PutMapping("/{id}/appointment")
    public ResponseEntity<ResponseMessage> rejectAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.rejectAppointment(id));
    }

    @PreAuthorize("{hasAnyRole('DOCTOR', 'ADMIN'), hasAuthority('EDIT')}")
    @PostMapping("/{id}/appointment")
    public ResponseEntity<ResponseMessage> completeAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.completeAppointment(id));
    }

    @PreAuthorize("{hasAnyRole('DOCTOR', 'ADMIN'), hasAuthority('EDIT')}")
    @PutMapping("/password")
    public ResponseEntity<ResponseMessage> editPassword(@RequestBody EditPasswordDTO userDTO) {
        return ResponseEntity.ok(userService.editPassword(userDTO));
    }

    @PreAuthorize("{hasAnyRole('DOCTOR', 'ADMIN'), hasAuthority('EDIT')}")
    @PutMapping
    public ResponseEntity<ResponseMessage> updateUser(@RequestBody EditUserDTO userDTO) {
        return ResponseEntity.ok(userService.editUser(userDTO));
    }
}
