package uz.medsu.sevice.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.medsu.entity.*;
import uz.medsu.enums.PaymentStatus;
import uz.medsu.event.BasketClearEvent;
import uz.medsu.payload.drugs.BasketDTO;
import uz.medsu.payload.drugs.ResponseDrugDTO;
import uz.medsu.repository.*;
import uz.medsu.sevice.BasketService;
import uz.medsu.utils.I18nUtil;
import uz.medsu.utils.ResponseMessage;
import uz.medsu.utils.Util;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasketServiceImpl implements BasketService {
    private final BasketRepository basketRepository;
    private final BasketDrugRepository basketDrugRepository;
    private final DrugRepository drugRepository;
    private final ApplicationEventPublisher eventPublisher;


    @Override
    public ResponseMessage addDrugInBasket(Long drugId, Integer drugCount) {
        if (drugCount == null || drugCount <= 0) throw new RuntimeException(I18nUtil.getMessage("drugCountIsNull"));
        Basket basket = getMyBasket();
        Drug drug = drugRepository.findById(drugId).orElseThrow(() -> new RuntimeException("drugNotFound"));
        Optional<BasketDrug> optionalBasketDrug = basketDrugRepository.findByDrug(drug);
        BasketDrug basketDrug;
        if (optionalBasketDrug.isEmpty()){
            basketDrug = BasketDrug.builder()
                    .count(drugCount)
                    .drug(drug)
                    .basket(basket)
                    .build();
            basketDrugRepository.save(basketDrug);
        } else {
            basketDrug = optionalBasketDrug.get();
            basketDrug.setCount(basketDrug.getCount() + drugCount);
            basketDrugRepository.save(basketDrug);
        }
        basket.setTotalPrice(basket.getTotalPrice() + drugCount*drug.getPrice());
        basketRepository.save(basket);

        BasketDTO basketDTO = new BasketDTO(
                basket.getId(),
                basket.getUser().getId(),
                basket.getTotalPrice(),
                parseDrugWithBasket(basket)
        );
        return ResponseMessage
                .builder()
                .success(true)
                .data(basketDTO)
                .build();
    }

    @Override
    public ResponseMessage deleteDrugInBasket(Long drugId) {
        Drug drug = drugRepository.findById(drugId).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("drugNotFound")));
        BasketDrug basketDrug = basketDrugRepository.findByDrug(drug).orElseThrow(() -> new RuntimeException(I18nUtil.getMessage("drugNotFound")));

        if (!basketDrug.getBasket().getUser().getId().equals(Util.getCurrentUser().getId())) throw new RuntimeException(I18nUtil.getMessage("drugNotFound"));

        basketDrugRepository.delete(basketDrug);
        return ResponseMessage
                .builder()
                .success(true)
                .message(I18nUtil.getMessage("drugDeletedSuccesses"))
                .build();
    }


    @Override
    public ResponseMessage getBasket() {
        Basket basket = getMyBasket();


        BasketDTO basketDTO = new BasketDTO(
                basket.getId(),
                basket.getUser().getId(),
                basket.getTotalPrice(),
                parseDrugWithBasket(basket)
        );
        return ResponseMessage
                .builder()
                .success(true)
                .data(basketDTO)
                .build();
    }


    // buyurtma qilmoqchi bulgan dorilarnning bazada bor yk yoqligini tekshiradi
    private List<Drug> checkDrugsCount(List<Drug> drugs) {
        List<Drug> result = new ArrayList<>();
        List<Drug> changeDrugsCount = new ArrayList<>();
        var all = drugRepository.findAll();
        for (Drug drug : drugs) {
            for (Drug drugInDb : all) {
                if (drug.getId().equals(drugInDb.getId())) {
                    if ((drugInDb.getQuantity() - 1) >= 0) {
                        drugInDb.setQuantity(drugInDb.getQuantity() - 1);
                        changeDrugsCount.add(drugInDb);
                    } else {
                        result.add(drug);
                    }
                }
            }
        }
        if (result.isEmpty()) {
            drugRepository.saveAll(changeDrugsCount);
        }
        return result;
    }


    private Basket getMyBasket() {
        Basket basket;
        Optional<Basket> optionalBasket = basketRepository.findByUser(Util.getCurrentUser());
        if (optionalBasket.isEmpty()) {
            basket = Basket
                    .builder()
                    .user(Util.getCurrentUser())
                    .totalPrice(0.0)
                    .build();
            basketRepository.save(basket);
            eventPublisher.publishEvent(new BasketClearEvent(basket));
        } else
            basket = optionalBasket.get();
        return basket;
    }

    private List<ResponseDrugDTO> parseDrugWithBasket(Basket basket){
        return basketDrugRepository.findByBasket(basket).stream().map(result -> {
            return new ResponseDrugDTO(
                    result.getDrug().getId(),
                    result.getDrug().getName(),
                    result.getDrug().getDescription(),
                    result.getDrug().getPrice() * result.getCount(),
                    result.getCount(),
                    result.getDrug().getImageUrl()
            );
        }).toList();
    }
}
