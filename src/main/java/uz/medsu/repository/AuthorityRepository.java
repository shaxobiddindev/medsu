package uz.medsu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.medsu.entity.Authority;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    boolean existsById(Long id);
}
