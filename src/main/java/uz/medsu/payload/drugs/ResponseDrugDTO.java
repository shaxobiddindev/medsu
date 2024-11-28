package uz.medsu.payload.drugs;

public record ResponseDrugDTO(
    Long drugId,
    String name,
    String description,
    Double price,
    Integer quantity,
    String urlImage
){}
