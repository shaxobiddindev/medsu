package uz.medsu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.medsu.entity.DrugOrder;

public interface OrderRepository extends JpaRepository<DrugOrder, Long> {
}
