package uz.medsu.sevice;

import uz.medsu.payload.chats.ChatDTO;
import uz.medsu.utils.ResponseMessage;

public interface ChatService {
    ResponseMessage addChat(ChatDTO chat);
    ResponseMessage getChat();
    ResponseMessage updateChat(String chatTitle,Long chatId);
    ResponseMessage deleteChat(Long chatId);
    ResponseMessage getChatById(Long id);
}
