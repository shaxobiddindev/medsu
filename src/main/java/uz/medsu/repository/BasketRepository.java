package uz.medsu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.medsu.entity.Basket;

public interface BasketRepository extends JpaRepository<Basket, Long> {
}
