package uz.medsu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.medsu.entity.Time;

public interface TimeRepository extends JpaRepository<Time, Integer> {

}
