package uz.medsu.sevice;

import uz.medsu.payload.drugs.DrugDTO;
import uz.medsu.utils.ResponseMessage;

public interface DrugService {
    ResponseMessage saveDrug(DrugDTO drugDTO);
    ResponseMessage editDrug(Long id,DrugDTO drugDTO);
    ResponseMessage deleteDrug( Long id);
    ResponseMessage getById( Long id);
    ResponseMessage getAllDrug(Integer page, Integer size);
}
