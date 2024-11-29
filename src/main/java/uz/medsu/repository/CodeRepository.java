package uz.medsu.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import uz.medsu.entity.Code;
import uz.medsu.enums.CodeType;

import java.util.Optional;

public interface CodeRepository extends JpaRepository<Code, Long> {
    Optional<Code> findByEmail(String email);

    Optional<Code> findByEmailAndType(String email, CodeType type);
}
