package uz.medsu.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.medsu.sevice.serviceImpl.UploadService;
import uz.medsu.utils.ResponseMessage;

@RestController
@RequiredArgsConstructor
@RequestMapping("/image")
public class UploadController {
    private final UploadService uploadService;

    @PostMapping(value = "/{id}/drug" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseMessage> uploadDrug(@RequestPart(value = "file") MultipartFile file, @PathVariable Long id) {
        return ResponseEntity.ok(uploadService.uploadDrug(file, id));
    }

    @PostMapping(value ="/{id}/user" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseMessage> uploadUser(@RequestPart(value = "file") MultipartFile file, @PathVariable Long id) {
        return ResponseEntity.ok(uploadService.uploadUser(file, id));
    }

    @PostMapping(value ="/{id}/article", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseMessage> uploadArticle(@RequestPart(value = "file") MultipartFile file, @PathVariable Long id) {
        return ResponseEntity.ok(uploadService.uploadArticle(file, id));
    }

    @DeleteMapping("/{id}/drug")
    public ResponseEntity<ResponseMessage> deleteImageDrug(@PathVariable Long id) {
        return ResponseEntity.ok(uploadService.deleteDrug(id));
    }

    @DeleteMapping("/{id}/user")
    public ResponseEntity<ResponseMessage> deleteImageUser(@PathVariable Long id) {
        return ResponseEntity.ok(uploadService.deleteUser(id));
    }

    @DeleteMapping("/{id}/article")
    public ResponseEntity<ResponseMessage> deleteImageArticle(@PathVariable Long id) {
        return ResponseEntity.ok(uploadService.deleteArticle(id));
    }
}

