package uz.medsu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.medsu.entity.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
}
