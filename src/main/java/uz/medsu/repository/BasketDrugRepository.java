package uz.medsu.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import uz.medsu.entity.Basket;
import uz.medsu.entity.BasketDrug;
import uz.medsu.entity.Drug;

import java.util.List;
import java.util.Optional;

public interface BasketDrugRepository extends JpaRepository<BasketDrug, Long> {
    List<BasketDrug> findByBasket(Basket basket);

    Optional<BasketDrug> findByDrug(Drug drug);

    boolean existsBasketDrugsByDrug(Drug drug);

    @Modifying
    @Transactional
    @Query("DELETE FROM BasketDrug b WHERE b.basket.id = :id")
    void deleteByBasket(Long id);
}
