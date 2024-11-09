package uz.medsu.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import uz.medsu.entity.User;

public interface Util {

    String[] openUrl = {
            "/auth/**",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    };

    static User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }


}
