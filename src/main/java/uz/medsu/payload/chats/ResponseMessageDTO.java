package uz.medsu.payload.chats;

import java.time.LocalDateTime;

public record ResponseMessageDTO(
        String message,
        Long replyId,
        Boolean isRead,
        Long senderId,
        LocalDateTime sendTime
) {
}
