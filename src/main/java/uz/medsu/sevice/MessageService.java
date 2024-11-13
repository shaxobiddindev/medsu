package uz.medsu.sevice;

import uz.medsu.entity.Message;
import uz.medsu.payload.EditMessageDTO;
import uz.medsu.payload.MessageDTO;
import uz.medsu.utils.ResponseMessage;

public interface MessageService {
    ResponseMessage sendMessage(MessageDTO message);
    ResponseMessage editMessage(EditMessageDTO message);
    ResponseMessage deleteMessage(Long id);
    ResponseMessage getMessage(Long id);
}
