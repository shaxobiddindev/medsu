package uz.medsu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.medsu.payload.drugs.DrugDTO;
import uz.medsu.sevice.DrugService;
import uz.medsu.sevice.serviceImpl.DrugServiceImpl;

@RestController
@RequestMapping("/drug")
@RequiredArgsConstructor
public class DrugController {
    private final DrugService drugService;

    @PreAuthorize("hasAuthority('POST')")
    @PostMapping
    public ResponseEntity<?> addDrug(@RequestBody DrugDTO drugDTO) {
        return ResponseEntity.ok(drugService.saveDrug(drugDTO));
    }

    @PreAuthorize("hasAuthority('READ')")
    @GetMapping
    public ResponseEntity<?> getAllDrugs(Integer page, Integer size) {
        return ResponseEntity.ok(drugService.getAllDrug(page, size));
    }

    @PreAuthorize("hasAuthority('READ')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getDrugById(@PathVariable Long id) {
        return ResponseEntity.ok(drugService.getById(id));
    }

    @PreAuthorize("hasAuthority('EDIT')")
    @PutMapping("/{id}")
    public ResponseEntity<?> editDrug(@PathVariable Long id,  @RequestBody DrugDTO drugDTO) {
        return ResponseEntity.ok(drugService.editDrug(id,drugDTO));
    }

    @PreAuthorize("hasAuthority('DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDrug(@PathVariable Long id) {
        return ResponseEntity.ok(drugService.deleteDrug(id));
    }

}
