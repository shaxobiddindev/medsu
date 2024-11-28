package uz.medsu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.medsu.entity.DrugClone;

import java.util.Optional;

public interface DrugCloneRepository extends JpaRepository<DrugClone, Long> {
    Optional<DrugClone> findByDrugId(Long id);
}
