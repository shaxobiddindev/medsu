package uz.medsu.payload.chats;

public record MessageDTO (
        Long chatId,
        String message,
        Long replyId
){
}
