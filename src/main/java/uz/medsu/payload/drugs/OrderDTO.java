package uz.medsu.payload.drugs;

public record OrderDTO(
        Long basketId,
        Double latitude,
        Double longitude
) {
}
