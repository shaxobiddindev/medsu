package uz.medsu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.medsu.entity.Doctor;

public interface SpecialityRepository extends JpaRepository<Doctor, Long> {

}
