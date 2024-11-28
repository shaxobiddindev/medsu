package uz.medsu.payload.drugs;

public record DrugDTO(
        String name,
        String description,
        Double price,
        Integer quantity
) {}
