package uz.medsu.sevice;

import uz.medsu.entity.Message;
import uz.medsu.payload.chats.EditMessageDTO;
import uz.medsu.payload.chats.MessageDTO;
import uz.medsu.utils.ResponseMessage;

public interface MessageService {
    Message sendMessage(MessageDTO message);
    ResponseMessage editMessage(EditMessageDTO message);
    ResponseMessage deleteMessage(Long id);
    ResponseMessage getMessage(Long id);
}
