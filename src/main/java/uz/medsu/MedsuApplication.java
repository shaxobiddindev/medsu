package uz.medsu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
public class MedsuApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedsuApplication.class, args);
    }

}
