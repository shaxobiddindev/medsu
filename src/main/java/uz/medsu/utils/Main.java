package uz.medsu.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        String  now = LocalDateTime.now().plusHours(5).toString();

        System.out.println(LocalDateTime.parse("2024-12-03T17:47:51.887548931"));
        System.out.println(now);
    }
}
