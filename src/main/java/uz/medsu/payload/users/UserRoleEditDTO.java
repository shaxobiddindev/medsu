package uz.medsu.payload.users;

import java.util.List;

public record UserRoleEditDTO(Long userId, String roleName, List<Long> authorityIds) {

}
