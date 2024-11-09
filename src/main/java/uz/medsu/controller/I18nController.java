package uz.medsu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.medsu.sevice.I18nService;
import uz.medsu.sevice.serviceImpl.I18nServiceImpl;
import uz.medsu.utils.I18nUtil;

@RestController
@RequestMapping("/lang")
@RequiredArgsConstructor
public class I18nController {
    private final I18nService i18nService;

    @PutMapping
    private ResponseEntity<?> setLanguage(@RequestParam String lang) {
        i18nService.setLanguage(lang);
        return ResponseEntity.ok(I18nUtil.getMessage("languageChanged"));
    }
}
