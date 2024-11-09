package uz.medsu.payload;

import java.util.List;

public record UserRoleEditDTO(Long userId, String roleName, List<Long> permissionsId) {
}
