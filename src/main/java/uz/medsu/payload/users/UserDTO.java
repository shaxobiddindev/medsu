package uz.medsu.payload.users;

public record UserDTO (
        String firstName,
        String lastName,
        Integer age,
        String email,
        String password,
        String gender
){}
