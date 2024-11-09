package uz.medsu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.medsu.entity.Chat;
import uz.medsu.repository.ChatRepository;
import uz.medsu.utils.ResponseMessage;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {
    private final ChatRepository chatRepository;

    @GetMapping
    public ResponseEntity<ResponseMessage> getChats(@RequestParam Long id) {
        return ResponseEntity.ok().build();
    }
}
