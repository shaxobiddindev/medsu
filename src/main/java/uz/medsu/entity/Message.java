package uz.medsu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.userdetails.UserDetails;
import uz.medsu.enums.MessageType;

import java.sql.Timestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String message;
    @ManyToOne
    private User sender;
    @CreationTimestamp
    private Timestamp sendTime;
    private Long replyId;
    @Enumerated(EnumType.STRING)
    private MessageType type;
    private Boolean isActive;
    private Boolean isRead;
}
