package uz.medsu.payload.users;

public record ForgotPasswordDTO(
        String email,
        String password,
        String token
) {
}
