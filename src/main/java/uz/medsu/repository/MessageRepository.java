package uz.medsu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.medsu.entity.Chat;
import uz.medsu.entity.Message;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {
    Optional<Message> findByIdAndIsActive(Long id, Boolean isActive);
    List<Message> findAllBySenderIdAndIsActive(Long id, Boolean isActive);
    List<Message> findAllByChatAndIsActive(Chat chat, Boolean isActive);
    List<Message> findAllByChat(Chat chat);
}
