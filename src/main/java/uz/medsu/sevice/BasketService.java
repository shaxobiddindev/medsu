package uz.medsu.sevice;

import uz.medsu.utils.ResponseMessage;

public interface BasketService {
    ResponseMessage addDrugInBasket(Long drugId, Integer drugCount);
    ResponseMessage deleteDrugInBasket(Long drugId);
    ResponseMessage getBasket();
}
