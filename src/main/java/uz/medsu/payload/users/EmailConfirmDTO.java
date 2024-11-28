package uz.medsu.payload.users;

public record EmailConfirmDTO(
        String email,
        String code
) {
}
