package uz.medsu.payload.drugs;

public record AddBasketDTO(
        Long drugId,
        Integer drugCount
) {
}
