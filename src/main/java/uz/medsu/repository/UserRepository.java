package uz.medsu.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import uz.medsu.entity.Role;
import uz.medsu.entity.User;
import uz.medsu.enums.Roles;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByProfession(Roles profession);

    Page<User> findAllByProfession(Roles profession, PageRequest pageRequest);
}
