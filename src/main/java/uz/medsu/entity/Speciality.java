package uz.medsu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.medsu.enums.DoctorSpeciality;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Speciality {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String about;
    private Long userId;
    @Enumerated(EnumType.STRING)
    private DoctorSpeciality doctorSpecialty;
    @ManyToMany
    private List<Day> workDays;
    @ManyToMany
    private List<Time> workTimes;
}