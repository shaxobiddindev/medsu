package uz.medsu.sevice;

public interface EmailService {
    void sendSimpleMessage(String to, String subject, String content);
    void sendCodeMessage(String to, String subject, String content);
}
