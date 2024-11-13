package uz.medsu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.medsu.payload.EditMessageDTO;
import uz.medsu.payload.MessageDTO;
import uz.medsu.sevice.MessageService;
import uz.medsu.utils.ResponseMessage;

@RestController
@RequiredArgsConstructor
@RequestMapping("/message")
public class MessageController {
    private final MessageService messageService;

    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage> getMessage(@PathVariable Long id) {
        return ResponseEntity.ok(messageService.getMessage(id));
    }

    @PostMapping
    public ResponseEntity<ResponseMessage> sendMessage(@RequestBody MessageDTO messageDTO) {
        return ResponseEntity.ok(messageService.sendMessage(messageDTO));
    }

    @PutMapping
    public ResponseEntity<ResponseMessage> sendMessage(@RequestBody EditMessageDTO messageDTO) {
        return ResponseEntity.ok(messageService.editMessage(messageDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessage> sendMessage(@PathVariable Long id) {
        return ResponseEntity.ok(messageService.deleteMessage(id));
    }
}
