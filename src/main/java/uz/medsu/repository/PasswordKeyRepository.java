package uz.medsu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.medsu.entity.PasswordKey;

import java.util.Optional;

public interface PasswordKeyRepository extends JpaRepository<PasswordKey, String> {
    Optional<PasswordKey> findByKey(String key);
}
