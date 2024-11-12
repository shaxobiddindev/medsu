package uz.medsu.payload;

import java.util.List;

public record ChatDTO(
        String title,
        String chatType,
        List<Long> userIds
) {
}
