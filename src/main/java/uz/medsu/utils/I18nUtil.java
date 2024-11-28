package uz.medsu.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import uz.medsu.entity.User;
import uz.medsu.sevice.I18nService;
import uz.medsu.sevice.serviceImpl.I18nServiceImpl;

import java.util.Locale;

@Component
public class I18nUtil {
    private static I18nService i18nService;
    I18nUtil(I18nService i18nService) {
        this.i18nService = i18nService;
    }
    public static String getMessage(String code){
        return i18nService.getMessage(code);
    }

    public static String getMessage(String code, User user){
        return i18nService.getMessage(code, user);
    }
}
