package uz.medsu.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        LocalDate  now = LocalDate.now();

        System.out.println(LocalDate.now().plusDays(0));
        System.out.println(LocalDate.now().plusDays(1));
        System.out.println(LocalDate.now().plusDays(3));
        System.out.println(now.getDayOfMonth());
    }
}
