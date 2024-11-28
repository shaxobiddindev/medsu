package uz.medsu.payload.users;

import uz.medsu.entity.Role;

public record ReturnUserDTO(
        Long id,
        String fistName,
        String lastName,
        String email,
        Integer age,
        String gender,
        Role role,
        Boolean enable,
        Boolean accountNonBlocked
) {
}
