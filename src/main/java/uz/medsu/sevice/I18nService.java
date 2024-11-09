package uz.medsu.sevice;

import jakarta.transaction.Transactional;

public interface I18nService {
    String getMessage(String key);

    @Transactional
    void setLanguage(String language);
}
