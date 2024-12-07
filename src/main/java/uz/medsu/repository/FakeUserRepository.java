package uz.medsu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.medsu.entity.FakeUser;

import java.util.Optional;

public interface FakeUserRepository extends JpaRepository<FakeUser, Long> {
    Optional<FakeUser> findByEmail(String email);
}
