package uz.medsu.payload.users;

import uz.medsu.entity.Role;

public record ReturnUserDTO(
        Long id,
        String fistName,
        String lastName,
        String username,
        String email,
        Integer age,
        String gender,
        Role role,
        Boolean enable,
        Boolean accountNonBlocked,
        String imageUrl
) {
}
