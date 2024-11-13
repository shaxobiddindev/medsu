package uz.medsu.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.medsu.entity.Chat;
import uz.medsu.entity.Message;
import uz.medsu.entity.User;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query(value = "SELECT c.* FROM chat c JOIN chat_users cu ON cu.chat_id = c.id WHERE cu.users_id = ?1 and c.is_active = true", nativeQuery = true)
    List<Chat> findAllByUser(Long userId);
    Optional<Chat> findByIdAndIsActive(Long id, Boolean isActive);
}
