package uz.medsu.sevice.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.medsu.entity.Chat;
import uz.medsu.entity.Message;
import uz.medsu.payload.chats.EditMessageDTO;
import uz.medsu.payload.chats.MessageDTO;
import uz.medsu.repository.ChatRepository;
import uz.medsu.repository.MessageRepository;
import uz.medsu.sevice.MessageService;
import uz.medsu.utils.I18nUtil;
import uz.medsu.utils.ResponseMessage;
import uz.medsu.utils.Util;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    @Override
    public Message sendMessage(MessageDTO messageDTO) {
        if (messageDTO.message().isBlank()) throw new RuntimeException("messageNotEmpty");
        Chat chat = chatRepository.findByIdAndIsActive(messageDTO.chatId(), true).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("chatNotFound")));
        Message message = Message.builder()
                .chat(chat)
                .sender(Util.getCurrentUser())
                .isRead(false)
                .isActive(true)
                .replyId(messageDTO.replyId() == null ? null : messageRepository.findByIdAndIsActive(messageDTO.chatId(), true).orElseThrow(()->new RuntimeException(I18nUtil.getMessage("messageNotFound"))).getId())
                .message(messageDTO.message())
                .build();
        messageRepository.save(message);
        return message;
    }

    @Override
    public ResponseMessage editMessage(EditMessageDTO messageDTO) {
        Message message = messageRepository.findByIdAndIsActive(messageDTO.messageId(), true).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("messageNotFound")));
        if (!message.getSender().getId().equals(Util.getCurrentUser().getId())) throw new RuntimeException(I18nUtil.getMessage("messageNotEdited"));
        message.setMessage(messageDTO.newMessage());
        messageRepository.save(message);
        return ResponseMessage.builder().success(true).message(I18nUtil.getMessage("messageEditedSuccess")).build();
    }

    @Override
    public ResponseMessage deleteMessage(Long id) {
        Message message = messageRepository.findByIdAndIsActive(id, true).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("messageNotFound")));
        if (!message.getSender().getId().equals(Util.getCurrentUser().getId())) throw new RuntimeException(I18nUtil.getMessage("messageNotDeleted"));
        message.setIsActive(false);
        messageRepository.save(message);
        return ResponseMessage.builder().success(true).message(I18nUtil.getMessage("messageDeletedSuccess")).build();
    }

    @Override
    public ResponseMessage getMessage(Long id) {
        Message message = messageRepository.findByIdAndIsActive(id, true).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("messageNotFound")));
        return ResponseMessage.builder().success(true).data(message).build();
    }
}
