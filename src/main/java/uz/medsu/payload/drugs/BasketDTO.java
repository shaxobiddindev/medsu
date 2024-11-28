package uz.medsu.payload.drugs;

import java.util.List;

public record BasketDTO(
        Long basketId,
        Long userId,
        Double amount,
        List<ResponseDrugDTO> drugs
) {}
