package uz.medsu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.medsu.entity.Doctor;
import uz.medsu.entity.Rating;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findAllByDoctorId(Long id);

    List<Rating> findAllByAppointmentId(Long id);

    @Query("SELECT SUM(r.rating) FROM Rating r WHERE r.doctorId = :id")
    Double sumRatingByDoctorId(@Param("id") Long id);
}
