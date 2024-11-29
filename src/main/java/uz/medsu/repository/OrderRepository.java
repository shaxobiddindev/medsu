package uz.medsu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.medsu.entity.DrugOrder;
import uz.medsu.entity.User;

import java.util.List;

public interface OrderRepository extends JpaRepository<DrugOrder, Long> {
    List<DrugOrder> findByUser(User user);
}
