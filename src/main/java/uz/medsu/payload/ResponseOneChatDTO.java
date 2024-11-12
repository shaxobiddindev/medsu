package uz.medsu.payload;

import java.util.List;

public record ResponseOneChatDTO (
    Long chatId,
    String title,
    String chatType,
    List<Long> userIds
){
}
