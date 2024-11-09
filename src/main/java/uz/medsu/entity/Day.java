package uz.medsu.entity;

import jakarta.persistence.*;
import lombok.Data;
import uz.medsu.enums.Days;

@Entity
@Data
public class Day {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Enumerated(EnumType.STRING)
    private Days day;
}
