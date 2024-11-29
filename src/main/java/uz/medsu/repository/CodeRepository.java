package uz.medsu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.medsu.entity.Code;
import uz.medsu.enums.CodeType;

import java.util.Optional;

public interface CodeRepository extends JpaRepository<Code, Long> {
    Optional<Code> findByEmailAndType(String email, CodeType type);
    Optional<Code> findByEmail(String email);
}
