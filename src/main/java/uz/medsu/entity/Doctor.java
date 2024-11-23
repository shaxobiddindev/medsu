package uz.medsu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.medsu.enums.DoctorSpeciality;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String about;
    @ManyToOne
    private User user;
    @Enumerated(EnumType.STRING)
    private DoctorSpeciality doctorSpecialty;
    private Double appointmentPrice;
    private Double rating;
}