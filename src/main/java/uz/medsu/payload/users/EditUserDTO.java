package uz.medsu.payload.users;

public record EditUserDTO(
        String firstName,
        String lastName,
        String gender,
        Integer age
) {}

