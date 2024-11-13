package uz.medsu.payload;

public record EditMessageDTO(
        Long messageId,
        String newMessage
) {
}
