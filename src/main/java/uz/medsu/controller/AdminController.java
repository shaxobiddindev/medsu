package uz.medsu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.medsu.payload.SetDoctorDTO;
import uz.medsu.payload.users.UserRoleEditDTO;
import uz.medsu.sevice.AdminService;
import uz.medsu.utils.ResponseMessage;

@RestController
@RequiredArgsConstructor
@PreAuthorize(value = "hasRole('ADMIN')")
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;

    @PreAuthorize(value = "hasAuthority('PERMISSION_CHANGE')")
    @GetMapping("/roles")
    public ResponseEntity<ResponseMessage> roles() {
        return ResponseEntity.ok(adminService.roles());
    }

    @PreAuthorize(value = "hasAuthority('SET_DOCTOR')")
    @PutMapping("/set-doctor")
    public ResponseEntity<ResponseMessage> setDoctor(@RequestBody SetDoctorDTO doctorDTO) {
        return ResponseEntity.ok(adminService.setDoctor(doctorDTO));
    }

    @PreAuthorize(value = "hasAuthority('SET_DOCTOR')")
    @PutMapping("/roles")
    public ResponseEntity<ResponseMessage> setRole(@RequestBody UserRoleEditDTO userDTO) {
        return ResponseEntity.ok(adminService.setRole(userDTO));
    }

    @PreAuthorize(value = "hasAnyAuthority('GET','EDIT','DELETE','READ','POST','SET_DOCTOR', 'BLOCK_USER','PERMISSION_CHANGE')")
    @GetMapping("/all-users")
    public ResponseEntity<ResponseMessage> getAllUsers(Integer page, Integer size) {
        return ResponseEntity.ok(adminService.getAllUsers(page, size));
    }

    @PreAuthorize(value = "hasAnyAuthority('GET','EDIT','DELETE','READ','POST','SET_DOCTOR', 'BLOCK_USER','PERMISSION_CHANGE')")
    @GetMapping("/all-admins")
    public ResponseEntity<ResponseMessage> getAllAdmins(Integer page, Integer size) {
        return ResponseEntity.ok(adminService.getAdmins(page, size));
    }

    @PreAuthorize(value = "hasAnyAuthority('GET','EDIT','DELETE','READ','POST','SET_DOCTOR', 'BLOCK_USER')")
    @GetMapping("/users")
    public ResponseEntity<ResponseMessage> getUsers(Integer page, Integer size) {
        return ResponseEntity.ok(adminService.getUsers(page, size));
    }

    @PreAuthorize(value = "hasAnyAuthority('GET','EDIT','DELETE','READ','POST','SET_DOCTOR', 'BLOCK_USER')")
    @GetMapping("/doctors")
    public ResponseEntity<ResponseMessage> getDoctors(Integer page, Integer size) {
        return ResponseEntity.ok(adminService.getDoctor(page, size));
    }

    @PutMapping("/user/{id}/block")
    public ResponseEntity<ResponseMessage> blockUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.blockUser(id));
    }

    @PutMapping("/user/{id}/unblock")
    public ResponseEntity<ResponseMessage> unblockUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.unblockUser(id));
    }

    @PutMapping("/user/{id}/enable")
    public ResponseEntity<ResponseMessage> enableUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.enableUser(id));
    }
}
