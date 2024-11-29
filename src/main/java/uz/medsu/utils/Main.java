package uz.medsu.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        LocalDateTime  now = LocalDateTime.now().minusMinutes(5);

        System.out.println(now.isBefore(LocalDateTime.now()));
        System.out.println(now);
    }
}
