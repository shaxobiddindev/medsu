package uz.medsu.payload.cards;

public record ResponseInvoiceDTO(
        Long id,
        String title,
        String description,
        Long toId,
        Long fromId,
        Double price,
        Double amount,
        String status,
        String crateTime,
        String updateTime
) {
}
