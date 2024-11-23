package uz.medsu.payload.cards;

public record CardDTO (
        String cardNumber,
        String expireDate,
        Double balance
){
}
