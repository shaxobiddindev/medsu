package uz.medsu.sevice.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uz.medsu.entity.Drug;
import uz.medsu.payload.drugs.DrugDTO;
import uz.medsu.payload.drugs.ResponseDrugDTO;
import uz.medsu.repository.BasketDrugRepository;
import uz.medsu.repository.DrugRepository;
import uz.medsu.sevice.DrugService;
import uz.medsu.utils.I18nUtil;
import uz.medsu.utils.ResponseMessage;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DrugServiceImpl implements DrugService {
    private final DrugRepository drugRepository;
    private final BasketDrugRepository basketDrugRepository;

    @Override
    public ResponseMessage saveDrug(DrugDTO drugDTO) {
        Drug drug = Drug
                .builder()
                .name(drugDTO.name())
                .description(drugDTO.description())
                .quantity(drugDTO.quantity() > 0 ? drugDTO.quantity() : 0)
                .price(drugDTO.price() > 0 ? drugDTO.price() : 0)
                .build();
        drugRepository.save(drug);
        return ResponseMessage
                .builder()
                .success(true)
                .data(
                        new ResponseDrugDTO(
                                drug.getId(),
                                drug.getName(),
                                drug.getDescription(),
                                drug.getPrice(),
                                drug.getQuantity(),
                                drug.getImageUrl()
                        )
                )
                .build();
    }


    @Override
    public ResponseMessage editDrug(Long id, DrugDTO drugDTO) {
        Drug drug = drugRepository.findById(id).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("drugNotFound")));
        if (basketDrugRepository.existsBasketDrugsByDrug(drug))
            throw new RuntimeException(I18nUtil.getMessage("cannotEditDrug"));

        drug.setName(drugDTO.name().isBlank() ? drug.getName() : drugDTO.name());
        drug.setDescription(drugDTO.description().isBlank() ? drug.getDescription() : drugDTO.description());
        drug.setPrice(drugDTO.price() == null || drugDTO.price() <= 0 ? drug.getPrice() : drugDTO.price());
        drug.setQuantity(drugDTO.quantity() == null || drugDTO.quantity() <= 0 ? drug.getQuantity() : drugDTO.quantity());

        drugRepository.save(drug);

        return ResponseMessage
                .builder()
                .success(true)
                .data(
                        new ResponseDrugDTO(
                                drug.getId(),
                                drug.getName(),
                                drug.getDescription(),
                                drug.getPrice(),
                                drug.getQuantity(),
                                drug.getImageUrl()
                        ))
                .build();
    }

    @Override
    public ResponseMessage deleteDrug(Long id) {
        Drug drug = drugRepository.findById(id).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("drugNotFound")));
        if (basketDrugRepository.existsBasketDrugsByDrug(drug))
            throw new RuntimeException(I18nUtil.getMessage("cannotDeleteDrug"));

        drugRepository.deleteById(id);
        return ResponseMessage
                .builder()
                .success(true)
                .message(I18nUtil.getMessage("drugDeletedSuccesses"))
                .build();
    }

    @Override
    public ResponseMessage getById(Long id) {
        Drug drug = drugRepository.findById(id).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("drugNotFound")));

        return ResponseMessage
                .builder()
                .success(true)
                .data(
                        new ResponseDrugDTO(
                                drug.getId(),
                                drug.getName(),
                                drug.getDescription(),
                                drug.getPrice(),
                                drug.getQuantity(),
                                drug.getImageUrl()
                        ))
                .build();
    }

    @Override
    public ResponseMessage getAllDrug(Integer page, Integer size) {
        List<ResponseDrugDTO> drugs = drugRepository.findAll(PageRequest.of(page, size)).stream().map(drug -> {
            return new ResponseDrugDTO(
                    drug.getId(),
                    drug.getName(),
                    drug.getDescription(),
                    drug.getPrice(),
                    drug.getQuantity(),
                    drug.getImageUrl()
            );
        }).toList();
        return ResponseMessage
                .builder()
                .success(true)
                .data(drugs)
                .build();
    }
}
