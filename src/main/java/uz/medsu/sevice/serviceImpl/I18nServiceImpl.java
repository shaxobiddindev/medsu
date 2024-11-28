package uz.medsu.sevice.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import uz.medsu.entity.User;
import uz.medsu.repository.UserRepository;
import uz.medsu.sevice.I18nService;
import uz.medsu.utils.Util;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class I18nServiceImpl implements I18nService {
    private final MessageSource messageSource;
    private final UserRepository userRepository;


    @Override
    public String getMessage(String key, User user) {
        return messageSource.getMessage(key, null, new Locale(user.getLocale()));
    }

    @Override
    public String getMessage(String key) {
        return messageSource.getMessage(key, null, new Locale(Util.getCurrentUser().getLocale()));
    }

    @Override
    public void setLanguage(String locale) {
        User currentUser = Util.getCurrentUser();
        currentUser.setLocale(locale);
        userRepository.save(currentUser);
    }
}
