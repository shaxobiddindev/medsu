package uz.medsu.payload.chats;

public record EditMessageDTO(
        Long messageId,
        String newMessage
) {
}
