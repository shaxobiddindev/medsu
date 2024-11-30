package uz.medsu.payload.cards;

public record TopUpCardDTO(
        String cardNumber,
        Double amount
) {
}
