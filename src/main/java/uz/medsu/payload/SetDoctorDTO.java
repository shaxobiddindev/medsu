package uz.medsu.payload;

import uz.medsu.entity.Doctor;

import java.util.List;

public record SetDoctorDTO(
        Long userId,
        List<Long> authoritiesId,
        String doctorSpeciality,
        String about,
        Double appointmentPrice
)
{}
