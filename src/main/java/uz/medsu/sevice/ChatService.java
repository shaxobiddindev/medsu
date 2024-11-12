package uz.medsu.sevice;

import uz.medsu.entity.Chat;
import uz.medsu.payload.ChatDTO;
import uz.medsu.utils.ResponseMessage;

public interface ChatService {
    ResponseMessage addChat(ChatDTO chat);
    ResponseMessage getChat();
    ResponseMessage updateChat(String chatTitle,Long chatId);
    ResponseMessage deleteChat(Long chatId);
    ResponseMessage getChatById(Long id);
}
