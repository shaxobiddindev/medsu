package uz.medsu.sevice;

import jakarta.transaction.Transactional;
import uz.medsu.entity.User;

public interface I18nService {
    String getMessage(String key, User user);
    String getMessage(String key);
    @Transactional
    void setLanguage(String language);
}
