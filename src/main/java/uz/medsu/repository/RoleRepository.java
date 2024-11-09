package uz.medsu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.medsu.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Role> {
}
