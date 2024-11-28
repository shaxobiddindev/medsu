package uz.medsu.payload;

public record EmailMessage(
        String to,
        String subject,
        String message
) {
}
