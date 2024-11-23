package uz.medsu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.medsu.payload.chats.ChatDTO;
import uz.medsu.sevice.ChatService;
import uz.medsu.utils.ResponseMessage;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<ResponseMessage> getChats() {
        return ResponseEntity.ok(chatService.getChat());
    }

    @PostMapping
    public ResponseEntity<ResponseMessage> addChat(@RequestBody ChatDTO chat) {
        return ResponseEntity.ok(chatService.addChat(chat));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseMessage> updateChat(@RequestBody String chatTitle, @PathVariable Long id) {
        return ResponseEntity.ok(chatService.updateChat(chatTitle, id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage> getChat(@PathVariable Long id) {
        return ResponseEntity.ok(chatService.getChatById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessage> deleteChat(@PathVariable Long id) {
        return ResponseEntity.ok(chatService.deleteChat(id));
    }
}
