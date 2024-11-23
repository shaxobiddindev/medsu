package uz.medsu.payload.users;

public record EditPasswordDTO(
        String oldPassword,
        String newPassword
) {
}
