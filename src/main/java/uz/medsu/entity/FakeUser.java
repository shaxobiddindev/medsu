package uz.medsu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.medsu.enums.Gender;
import uz.medsu.enums.Roles;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FakeUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private Integer age;
    private String password;
    @Column(unique = true)
    private String email;
    private String username;
    private String phone;
}
