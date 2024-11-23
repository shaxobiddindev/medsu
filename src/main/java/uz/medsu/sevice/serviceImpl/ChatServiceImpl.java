package uz.medsu.sevice.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.medsu.entity.Chat;
import uz.medsu.entity.User;
import uz.medsu.enums.ChatType;
import uz.medsu.payload.chats.ChatDTO;
import uz.medsu.payload.chats.ResponseChatDTO;
import uz.medsu.payload.chats.ResponseMessageDTO;
import uz.medsu.payload.chats.ResponseOneChatDTO;
import uz.medsu.repository.ChatRepository;
import uz.medsu.repository.MessageRepository;
import uz.medsu.repository.UserRepository;
import uz.medsu.sevice.ChatService;
import uz.medsu.utils.I18nUtil;
import uz.medsu.utils.ResponseMessage;
import uz.medsu.utils.Util;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Override
    public ResponseMessage addChat(ChatDTO chat) {
        if (ChatType.valueOf(chat.chatType().toUpperCase()).equals(ChatType.PERSON)){
            List<Chat> chats = chatRepository.findAllByUser(userRepository.findById(chat.userIds().get(0)).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("userNotFound"))).getId());
            for (Chat temp : chats) {
                if (temp.getUsers().contains(Util.getCurrentUser())) {
                    List<ResponseMessageDTO> messages = messageRepository.findAllByChatAndIsActive(temp, true).stream().map(message -> {
                        return new ResponseMessageDTO(
                                message.getMessage(),
                                message.getReplyId(),
                                message.getIsRead(),
                                message.getSender().getId(),
                                message.getSendTime().toLocalDateTime()
                        );
                    }).toList();
                    ResponseChatDTO responseChatDTO = new ResponseChatDTO(
                            temp.getId(),
                            temp.getTitle(),
                            temp.getChatType().toString(),
                            temp.getUsers().stream().map(User::getId).toList(),
                            messages
                            );
                    return new ResponseMessage(true, null, responseChatDTO);
                }

            }
        }
        if (ChatType.valueOf(chat.chatType().toUpperCase()  ).equals(ChatType.PERSON)&& chat.userIds().size() > 1) throw new RuntimeException(I18nUtil.getMessage("chatAddUserError"));
        Chat chatEntity = Chat.builder()
                .chatType(ChatType.valueOf(chat.chatType().toUpperCase()))
                .title(chat.title())
                .isActive(true)
                .build();
        List<User> users = chat.userIds().stream().map(id -> {
            return userRepository.findById(id).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("userNotFound")));
        }).toList();
        users.add(Util.getCurrentUser());
        chatEntity.setUsers(users);
        chatRepository.save(chatEntity);
        ResponseChatDTO responseChatDTO = new ResponseChatDTO(
                chatEntity.getId(),
                chatEntity.getTitle(),
                chatEntity.getChatType().toString(),
                chatEntity.getUsers().stream().map(User::getId).toList(),
                new ArrayList<>()
        );
        return new ResponseMessage(true, null, responseChatDTO);
    }

    @Override
    public ResponseMessage getChat() {
        List<ResponseOneChatDTO> responseOneChatDTOS = chatRepository.findAllByUser(Util.getCurrentUser().getId()).stream().map(chat -> {
            return new ResponseOneChatDTO(
                    chat.getId(),
                    chat.getTitle(),
                    chat.getChatType().toString(),
                    chat.getUsers().stream().map(User::getId).toList()
            );
        }).toList();
        return new ResponseMessage(true, null, responseOneChatDTOS);
    }

    @Override
    public ResponseMessage updateChat(String chatTitle, Long chatId) {
        Chat found = chatRepository.findById(chatId).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("chatNotFound")));
        found.setTitle(chatTitle);
        chatRepository.save(found);
        ResponseOneChatDTO responseOneChatDTO = new ResponseOneChatDTO(
                found.getId(),
                found.getTitle(),
                found.getChatType().toString(),
                found.getUsers().stream().map(User::getId).toList()
        );
        return new ResponseMessage(true, null, responseOneChatDTO);
    }

    @Override
    public ResponseMessage deleteChat(Long chatId) {
        Chat found = chatRepository.findById(chatId).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("chatNotFound")));
        if (!found.getIsActive()) throw new RuntimeException(I18nUtil.getMessage("chatNotFound"));
        chatRepository.deleteById(chatId);
        return new ResponseMessage(true, I18nUtil.getMessage("chatDeletedSuccess"), chatId);
    }

    @Override
    public ResponseMessage getChatById(Long id) {
        Chat found = chatRepository.findById(id).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("chatNotFound")));
        ResponseChatDTO responseChatDTO = new ResponseChatDTO(
                found.getId(),
                found.getTitle(),
                found.getChatType().toString(),
                found.getUsers().stream().map(User::getId).toList(),
                messageRepository.findAllByChatAndIsActive(found, true).stream().map(message -> {
                    message.setIsRead(true);
                    messageRepository.save(message);
                    return new ResponseMessageDTO(
                            message.getMessage(),
                            message.getReplyId(),
                            message.getIsRead(),
                            message.getSender().getId(),
                            message.getSendTime().toLocalDateTime()
                    );
                }).toList()
                );
        return new ResponseMessage(true, null, responseChatDTO);
    }
}
