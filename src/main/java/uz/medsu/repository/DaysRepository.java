package uz.medsu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.medsu.entity.Day;
import uz.medsu.enums.Days;

import java.util.Optional;

public interface DaysRepository extends JpaRepository<Day, Integer> {
    Optional<Day> findByDay(Days days);
}
