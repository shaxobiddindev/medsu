package uz.medsu.payload;

import uz.medsu.entity.Role;
import uz.medsu.enums.Gender;

public record ReturnUserDTO(Long id, String fistName, String lastName, String email, Integer age, String gender, Role role) {
}
