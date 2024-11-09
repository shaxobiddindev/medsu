package uz.medsu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.medsu.entity.Speciality;

public interface SpecialityRepository extends JpaRepository<Speciality, Long> {
}
