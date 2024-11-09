package uz.medsu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.medsu.entity.Card;

public interface CardRepository extends JpaRepository<Card, Long> {
}
