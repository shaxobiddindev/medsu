package uz.medsu.payload.doctors;

public record ResponseDoctorDTO(
        Long id,
        String about,
        String firstName,
        String lastName,
        String speciality,
        Double price,
        Double rating,
        Double latitude,
        Double longitude,
        String imageUrl
) {}
