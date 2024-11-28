package uz.medsu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.medsu.payload.drugs.AddBasketDTO;
import uz.medsu.sevice.BasketService;
import uz.medsu.sevice.serviceImpl.BasketServiceImpl;
import uz.medsu.utils.ResponseMessage;

@RestController
@RequestMapping("/basket")
@RequiredArgsConstructor
public class BasketController {
    private final BasketService basketService;

    @PostMapping
    public ResponseEntity<ResponseMessage> addBasket(@RequestBody AddBasketDTO basketDTO) {
        return ResponseEntity.ok(basketService.addDrugInBasket(basketDTO.drugId(), basketDTO.drugCount()));
    }

    @DeleteMapping("/{id}/drug")
    public ResponseEntity<ResponseMessage> removeDrugInBasket(@PathVariable Long id) {
        return ResponseEntity.ok(basketService.deleteDrugInBasket(id));
    }


    @GetMapping
    public ResponseEntity<?> getBasket() {
        return ResponseEntity.ok(basketService.getBasket());
    }
}
