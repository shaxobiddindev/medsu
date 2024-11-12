package uz.medsu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.medsu.entity.Chat;
import uz.medsu.entity.Message;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllBySenderId(Long id);
    List<Message> findAllByChatAndIsActive(Chat chat, Boolean isActive);
    List<Message> findAllByChat(Chat chat);
}
