package uz.medsu.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.medsu.entity.Appointment;
import uz.medsu.entity.Doctor;
import uz.medsu.entity.Invoice;
import uz.medsu.entity.User;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    Optional<Appointment> findByDateAndTime(Timestamp date, String time);

    Page<Appointment> findAllByUser(User user, PageRequest pageRequest);
    Page<Appointment> findAllByDoctor(Doctor doctor, PageRequest pageRequest);

    Optional<Appointment> findByInvoice(Invoice invoice);
}
