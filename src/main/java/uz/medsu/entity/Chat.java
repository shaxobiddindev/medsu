package uz.medsu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.medsu.enums.ChatType;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Enumerated(EnumType.STRING)
    private ChatType chatType;
    @ManyToMany
    @JoinTable(
            name = "chat_users", // specify join table name
            joinColumns = @JoinColumn(name = "chat_id"), // specify Chat's column in join table
            inverseJoinColumns = @JoinColumn(name = "users_id") // specify User's column in join table
    )
    private List<User> users;
    private Boolean isActive;
}
