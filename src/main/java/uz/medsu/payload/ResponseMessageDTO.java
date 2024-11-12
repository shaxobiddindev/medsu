package uz.medsu.payload;

import java.time.LocalDateTime;

public record ResponseMessageDTO(
        String message,
        String type,
        Long replyId,
        Boolean isRead,
        Long senderId,
        LocalDateTime sendTime
) {
}
