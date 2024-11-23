package uz.medsu.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.medsu.entity.Doctor;
import uz.medsu.entity.User;

import java.util.List;
import java.util.Optional;

public interface SpecialityRepository extends JpaRepository<Doctor, Long> {

    Optional<Doctor> findByUser(User user);

    Page<Doctor> findAllByOrderByRatingDesc(PageRequest pageRequest);

    @Query("SELECT d FROM Doctor d WHERE LOWER(d.user.firstName) LIKE LOWER(CONCAT('%', :text, '%')) OR LOWER(d.user.lastName) LIKE LOWER(CONCAT('%', :text, '%')) OR LOWER(d.doctorSpecialty) LIKE LOWER(CONCAT('%', :text, '%'))")
    Page<Doctor> searchDoctorsByUserFirstNameOrLastNameOrSpecialty(@Param("text") String text, PageRequest pageRequest);
}
