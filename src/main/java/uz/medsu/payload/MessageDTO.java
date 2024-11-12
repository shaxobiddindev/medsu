package uz.medsu.payload;

import java.time.LocalDateTime;

public record MessageDTO (
        Long chatId,
        String message,
        String type,
        Long replyId
){
}
