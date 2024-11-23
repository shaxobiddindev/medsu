package uz.medsu.payload.chats;

import java.util.List;

public record ResponseChatDTO (
        Long chatId,
        String title,
        String chatType,
        List<Long> userIds,
        List<ResponseMessageDTO> messages
) {
}
