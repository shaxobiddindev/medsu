package uz.medsu.payload.cards;

public record ResponseCardDTO(
        Long id,
        String number,
        String expire,
        Double balance
) {
}
