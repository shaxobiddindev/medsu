package uz.medsu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.medsu.entity.Drug;

public interface DrugRepository extends JpaRepository<Drug, Long> {
}
